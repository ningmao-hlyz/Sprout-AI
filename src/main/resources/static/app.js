// 获取 DOM 元素
const chatContainer = document.getElementById('chatContainer');
const userInput = document.getElementById('userInput');
const sendButton = document.getElementById('sendButton');

let isLoading = false;

// 为整个回答添加复制按钮（全局可用）
function addCopyAllButton(container, fullText) {
    if (!container || !fullText) return;

    // 避免重复添加
    if (container.querySelector('.copy-all-btn')) return;

    const btn = document.createElement('button');
    btn.className = 'copy-all-btn';
    btn.innerText = '复制全部';

    btn.addEventListener('click', async (e) => {
        e.stopPropagation();
        try {
            await navigator.clipboard.writeText(fullText);
            const oldText = btn.innerText;
            btn.innerText = '已复制';
            btn.classList.add('copied');
            setTimeout(() => {
                btn.innerText = oldText;
                btn.classList.remove('copied');
            }, 2000);
        } catch (err) {
            console.error('复制失败', err);
            btn.innerText = '复制失败';
        }
    });

    container.appendChild(btn);
}

// 初始化
document.addEventListener('DOMContentLoaded', () => {
    // 检查库是否加载
    if (typeof marked === 'undefined' || typeof hljs === 'undefined') {
        console.error("库加载失败，请检查网络或 CDN");
    }

    if(sendButton) sendButton.addEventListener('click', handleSendMessage);
    if(userInput) {
        userInput.addEventListener('keydown', (e) => {
            if (e.key === 'Enter' && !e.shiftKey) {
                e.preventDefault();
                handleSendMessage();
            }
        });
        userInput.addEventListener('input', function() {
            this.style.height = 'auto';
            if (this.scrollHeight > 150) {
                this.style.overflowY = 'auto';
            } else {
                this.style.overflowY = 'hidden';
            }
            this.style.height = Math.min(this.scrollHeight, 150) + 'px';
        });
        userInput.focus();
    }

    // 提示词点击
    document.querySelectorAll('.hint').forEach(hint => {
        hint.addEventListener('click', () => {
            const text = hint.textContent.trim().replace(/^[\u{1F300}-\u{1F6FF}\u{2600}-\u{26FF}\u{2700}-\u{27BF}]\s*/u, '');
            userInput.value = text;
            handleSendMessage();
        });
    });
});

async function handleSendMessage() {
    console.log("点击触发！");
    const text = userInput.value.trim();
    if (!text || isLoading) return;

    userInput.value = '';
    userInput.style.height = 'auto';
    isLoading = true;
    sendButton.disabled = true;

    addMessage(text, 'user');
    
    try {
        // 根据复选框选择流式或非流式
        const useStream = document.getElementById('useStream')?.checked ?? true;
        if (useStream) {
            await handleStreamChat(text);
        } else {
            await handleNonStreamChat(text);
        }
    } catch (error) {
        console.error('发送消息失败:', error);
    } finally {
        // 确保无论成功失败都重置状态
        isLoading = false;
        sendButton.disabled = false;
        userInput.focus();
    }
}

// 非流式聊天处理
async function handleNonStreamChat(message) {
    // 显示加载气泡
    const loadingId = addLoadingMessage();
    try {
        const response = await fetch('/api/chat', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ message })
        });

        if (!response.ok) throw new Error(`Status: ${response.status}`);

        const data = await response.json();
        const aiReply = data.answer || data.content || "无内容";

        // 移除加载气泡，展示真正回答
        removeMessage(loadingId);
        addMessage(aiReply, 'assistant');

    } catch (error) {
        console.error('非流式输出错误:', error);
        // 移除加载气泡后再显示错误消息
        removeMessage(loadingId);
        addMessage(` 出错了: ${error.message}`, 'assistant');
        throw error;
    }
}

// 流式聊天处理
async function handleStreamChat(message) {
    // 创建一个空的助手消息，用于逐字追加
    const messageId = `stream-${Date.now()}`;
    const messageDiv = createStreamMessage(messageId);
    chatContainer.appendChild(messageDiv);
    scrollToBottom();
    
    const textDiv = messageDiv.querySelector('.message-text');
    let fullContent = '';
    
    try {
        const response = await fetch('/api/chat/stream', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ message })
        });

        if (!response.ok) throw new Error(`Status: ${response.status}`);

        const reader = response.body.getReader();
        const decoder = new TextDecoder();
        let buffer = '';

        while (true) {
            const { done, value } = await reader.read();
            if (done) break;

            buffer += decoder.decode(value, { stream: true });
            const lines = buffer.split('\n');
            buffer = lines.pop() || ''; // 保留不完整的行

            for (const line of lines) {
                if (line.startsWith('data: ')) {
                    const data = JSON.parse(line.slice(6));
                    
                    if (data.type === 'content') {
                        // 追加内容
                        fullContent += data.content;
                        updateStreamMessage(textDiv, fullContent);
                        scrollToBottom();
                    } else if (data.type === 'done') {
                        // 流式输出完成，应用最终格式化
                        finalizeStreamMessage(textDiv, fullContent);
                    }
                }
            }
        }

    } catch (error) {
        console.error('流式输出错误:', error);
        textDiv.innerHTML = `<span style="color: #e74c3c;"> 出错了: ${error.message}</span>`;
        // 抛出错误，让外层的 finally 可以重置按钮状态
        throw error;
    }
}

// 创建流式消息容器
function createStreamMessage(id) {
    const messageDiv = document.createElement('div');
    messageDiv.id = id;
    messageDiv.className = 'message assistant';

    const avatar = document.createElement('div');
    avatar.className = 'message-avatar';
    avatar.innerHTML = '🌱';

    const content = document.createElement('div');
    content.className = 'message-content';

    const textDiv = document.createElement('div');
    textDiv.className = 'message-text';

    content.appendChild(textDiv);
    messageDiv.appendChild(avatar);
    messageDiv.appendChild(content);

    return messageDiv;
}

// 更新流式消息（实时追加）
function updateStreamMessage(textDiv, content) {
    // 实时用 Markdown 渲染，但不做代码高亮和复制按钮，避免频繁重排太重
    if (typeof marked !== 'undefined') {
        const rawHtml = marked.parse(content);
        const safeHtml = (typeof DOMPurify !== 'undefined')
            ? DOMPurify.sanitize(rawHtml)
            : rawHtml;
        textDiv.innerHTML = safeHtml;
    } else {
        // 退化为纯文本
        textDiv.textContent = content;
    }

    // 追加一个光标，提示仍在输出中
    const cursor = document.createElement('span');
    cursor.className = 'cursor';
    textDiv.appendChild(cursor);
}

// 完成流式消息（应用 Markdown 渲染和代码高亮）
function finalizeStreamMessage(textDiv, content) {
    // 移除光标
    const cursor = textDiv.querySelector('.cursor');
    if (cursor) cursor.remove();
    
    // 应用 Markdown 渲染
    if (typeof marked !== 'undefined') {
        const rawHtml = marked.parse(content);
        const safeHtml = (typeof DOMPurify !== 'undefined')
            ? DOMPurify.sanitize(rawHtml)
            : rawHtml;
        textDiv.innerHTML = safeHtml;

        // 为整条流式回答添加复制按钮
        addCopyAllButton(textDiv.closest('.message-content'), content);

        // 代码高亮 + 复制按钮
        if (typeof hljs !== 'undefined') {
            textDiv.querySelectorAll('pre code').forEach((block) => {
                hljs.highlightElement(block);

                const pre = block.parentElement;
                const copyBtn = document.createElement('button');
                copyBtn.className = 'copy-btn';
                copyBtn.innerText = '复制';

                copyBtn.addEventListener('click', async () => {
                    try {
                        await navigator.clipboard.writeText(block.textContent);
                        copyBtn.innerText = '已复制!';
                        copyBtn.classList.add('copied');
                        setTimeout(() => {
                            copyBtn.innerText = '复制';
                            copyBtn.classList.remove('copied');
                        }, 2000);
                    } catch (err) {
                        console.error('复制失败', err);
                        copyBtn.innerText = '失败';
                    }
                });

                pre.appendChild(copyBtn);
            });
        }
    } else {
        textDiv.innerHTML = content.replace(/\n/g, '<br>');
        addCopyAllButton(textDiv.closest('.message-content'), content);
    }
}

function addMessage(text, type) {
    const messageDiv = document.createElement('div');
    messageDiv.className = `message ${type}`;

    const avatarHtml = type === 'user'
        ? `<svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path><circle cx="12" cy="7" r="4"></circle></svg>`
        : `🌱`;

    const avatar = document.createElement('div');
    avatar.className = 'message-avatar';
    avatar.innerHTML = avatarHtml;

    const content = document.createElement('div');
    content.className = 'message-content';

    const textDiv = document.createElement('div');
    textDiv.className = 'message-text';

    if (type === 'assistant') {
        if (typeof marked !== 'undefined') {
            // 1. Markdown 解析
            const rawHtml = marked.parse(text);
            const safeHtml = (typeof DOMPurify !== 'undefined')
                ? DOMPurify.sanitize(rawHtml)
                : rawHtml;
            textDiv.innerHTML = safeHtml;

            // 非流式助手回答添加复制按钮
            addCopyAllButton(content, text);

            // 2. 代码高亮 + 添加复制按钮 (关键步骤)
            if (typeof hljs !== 'undefined') {
                textDiv.querySelectorAll('pre code').forEach((block) => {
                    // 应用高亮
                    hljs.highlightElement(block);

                    // 获取父级 <pre> 元素
                    const pre = block.parentElement;

                    // 创建复制按钮
                    const copyBtn = document.createElement('button');
                    copyBtn.className = 'copy-btn';
                    copyBtn.innerText = '复制';

                    // 绑定点击事件
                    copyBtn.addEventListener('click', async () => {
                        try {
                            await navigator.clipboard.writeText(block.textContent);
                            // 复制成功反馈
                            copyBtn.innerText = '已复制!';
                            copyBtn.classList.add('copied');
                            setTimeout(() => {
                                copyBtn.innerText = '复制';
                                copyBtn.classList.remove('copied');
                            }, 2000);
                        } catch (err) {
                            console.error('复制失败', err);
                            copyBtn.innerText = '失败';
                        }
                    });

                    // 将按钮添加到代码块容器中
                    pre.appendChild(copyBtn);
                });
            }
        } else {
            textDiv.innerHTML = text.replace(/\n/g, '<br>');
            addCopyAllButton(content, text);
        }
    } else {
        textDiv.textContent = text;
    }

    content.appendChild(textDiv);
    messageDiv.appendChild(avatar);
    messageDiv.appendChild(content);
    chatContainer.appendChild(messageDiv);
    scrollToBottom();
}

function addLoadingMessage() {
    const id = `loading-${Date.now()}`;
    const html = `
        <div class="message assistant" id="${id}">
            <div class="message-avatar">🌱</div>
            <div class="message-content">
                <div class="message-text">
                    <div class="loading"><span></span><span></span><span></span></div>
                </div>
            </div>
        </div>`;
    chatContainer.insertAdjacentHTML('beforeend', html);
    scrollToBottom();
    return id;
}

function removeMessage(id) {
    const el = document.getElementById(id);
    if (el) el.remove();
}

function scrollToBottom() {
    setTimeout(() => {
        chatContainer.scrollTop = chatContainer.scrollHeight;
    }, 10);
}
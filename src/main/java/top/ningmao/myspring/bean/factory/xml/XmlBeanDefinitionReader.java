package top.ningmao.myspring.bean.factory.xml;

import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.XmlUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import top.ningmao.myspring.bean.BeansException;
import top.ningmao.myspring.bean.PropertyValue;
import top.ningmao.myspring.bean.factory.config.BeanDefinition;
import top.ningmao.myspring.bean.factory.config.BeanReference;
import top.ningmao.myspring.bean.factory.support.AbstractBeanDefinitionReader;
import top.ningmao.myspring.bean.factory.support.BeanDefinitionRegistry;
import top.ningmao.myspring.core.io.Resource;
import top.ningmao.myspring.core.io.ResourceLoader;

import java.io.IOException;
import java.io.InputStream;

/**
 * 读取配置再xml中的bean定义信息
 *
 * @author ningmao
 * @since 2025-5-8
 */
public class XmlBeanDefinitionReader extends AbstractBeanDefinitionReader {
    
    public static final String BEAN_ELEMENT = "bean";
    public static final String PROPERTY_ELEMENT = "property";
    public static final String ID_ATTRIBUTE = "id";
    public static final String NAME_ATTRIBUTE = "name";
    public static final String CLASS_ATTRIBUTE = "class";
    public static final String VALUE_ATTRIBUTE = "value";
    public static final String REF_ATTRIBUTE = "ref";
    public static final String INIT_METHOD_ATTRIBUTE = "init-method";
    public static final String DESTROY_METHOD_ATTRIBUTE = "destroy-method";
    public static final String SCOPE_ATTRIBUTE = "scope";
    
    public XmlBeanDefinitionReader(BeanDefinitionRegistry registry) {
        super(registry);
    }
    
    public XmlBeanDefinitionReader(BeanDefinitionRegistry registry, ResourceLoader resourceLoader) {
        super(registry, resourceLoader);
    }
    
    
    @Override
    public void loadBeanDefinitions(Resource resource) throws BeansException {
        try {
            InputStream is = resource.getInputStream();
            try {
                doLoadBeanDefinitions(is);
            } finally {
                is.close();
            }
        } catch (IOException e) {
            throw new BeansException("IOException parsing XML document from " + resource, e);
        }
    }
    
    @Override
    public void loadBeanDefinitions(String location) throws BeansException {
        ResourceLoader resourceLoader = getResourceLoader();
        Resource resource = resourceLoader.getResource(location);
        loadBeanDefinitions(resource);
    }
    
    
    /**
     * 解析 XML 中的 bean 定义，并注册到 BeanDefinitionRegistry 中
     *
     * @param inputStream 输入流，读取 XML 配置文件内容
     */
    protected void doLoadBeanDefinitions(InputStream inputStream) {
        // 解析 XML，得到文档对象
        Document document = XmlUtil.readXML(inputStream);
        
        // 获取根元素 <beans>
        Element root = document.getDocumentElement();
        
        // 获取根元素下的所有子节点（包括空白、换行、注释、<bean>等）
        NodeList childNodes = root.getChildNodes();
        
        // 遍历所有子节点
        for (int i = 0; i < childNodes.getLength(); i++) {
            // 只处理元素类型的节点（排除空白或注释）
            if (childNodes.item(i) instanceof Element) {
                // 判断是否是 <bean> 标签
                if (BEAN_ELEMENT.equals(childNodes.item(i).getNodeName())) {
                    // 强转为 Element 类型
                    Element bean = (Element) childNodes.item(i);
                    
                    // 获取 <bean> 标签的属性
                    String id = bean.getAttribute(ID_ATTRIBUTE);         // bean 的唯一标识 id
                    String name = bean.getAttribute(NAME_ATTRIBUTE);     // 备用名称 name
                    String className = bean.getAttribute(CLASS_ATTRIBUTE); // bean 的全类名 class
                    String initMethodName = bean.getAttribute(INIT_METHOD_ATTRIBUTE);
                    String destroyMethodName = bean.getAttribute(DESTROY_METHOD_ATTRIBUTE);
                    String beanScope = bean.getAttribute(SCOPE_ATTRIBUTE);
                    // 通过反射获取 Class 对象
                    Class<?> clazz = null;
                    try {
                        clazz = Class.forName(className);
                    } catch (ClassNotFoundException e) {
                        throw new BeansException("Cannot find class [" + className + "]");
                    }
                    
                    // 优先使用 id，其次使用 name，最后使用类名首字母小写
                    String beanName = StrUtil.isNotEmpty(id) ? id : name;
                    if (StrUtil.isEmpty(beanName)) {
                        // 如果 id 和 name 都为空，则使用类名首字母小写作为默认名称
                        beanName = StrUtil.lowerFirst(clazz.getSimpleName());
                    }
                    
                    // 创建 BeanDefinition 对象，记录 bean 的 class 信息
                    BeanDefinition beanDefinition = new BeanDefinition(clazz);
                    beanDefinition.setInitMethodName(initMethodName);
                    beanDefinition.setDestroyMethodName(destroyMethodName);
                    if (StrUtil.isNotEmpty(beanScope)) {
                        beanDefinition.setScope(beanScope);
                    }
                    
                    
                    // 处理 <bean> 标签内部的 <property> 子标签
                    for (int j = 0; j < bean.getChildNodes().getLength(); j++) {
                        if (bean.getChildNodes().item(j) instanceof Element) {
                            Element property = (Element) bean.getChildNodes().item(j);
                            // 判断是否为 <property> 标签
                            if (PROPERTY_ELEMENT.equals(property.getNodeName())) {
                                // 获取 property 的 name、value、ref 属性
                                String nameAttribute = property.getAttribute(NAME_ATTRIBUTE);
                                String valueAttribute = property.getAttribute(VALUE_ATTRIBUTE);
                                String refAttribute = property.getAttribute(REF_ATTRIBUTE);
                                
                                // name 是必须的
                                if (StrUtil.isEmpty(nameAttribute)) {
                                    throw new BeansException("The name attribute cannot be null or empty");
                                }
                                
                                // 属性值的处理逻辑：如果有 ref，则创建 BeanReference，否则用 value
                                Object value = valueAttribute;
                                if (StrUtil.isNotEmpty(refAttribute)) {
                                    value = new BeanReference(refAttribute);
                                }
                                
                                // 创建 PropertyValue 并添加到 beanDefinition 中
                                PropertyValue propertyValue = new PropertyValue(nameAttribute, value);
                                beanDefinition.getPropertyValues().addPropertyValue(propertyValue);
                            }
                        }
                    }
                    
                    // 检查是否存在重复的 beanName，防止重复注册
                    if (getRegistry().containsBeanDefinition(beanName)) {
                        throw new BeansException("Duplicate beanName[" + beanName + "] is not allowed");
                    }
                    
                    // 将解析完成的 BeanDefinition 注册到容器中
                    getRegistry().registerBeanDefinition(beanName, beanDefinition);
                }
            }
        }
    }
}

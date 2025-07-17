package top.ningmao.myspring.bean.factory.xml;

import cn.hutool.core.util.StrUtil;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import top.ningmao.myspring.bean.BeansException;
import top.ningmao.myspring.bean.PropertyValue;
import top.ningmao.myspring.bean.factory.config.BeanDefinition;
import top.ningmao.myspring.bean.factory.config.BeanReference;
import top.ningmao.myspring.bean.factory.support.AbstractBeanDefinitionReader;
import top.ningmao.myspring.bean.factory.support.BeanDefinitionRegistry;
import top.ningmao.myspring.context.annotation.ClassPathBeanDefinitionScanner;
import top.ningmao.myspring.core.io.Resource;
import top.ningmao.myspring.core.io.ResourceLoader;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

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
    public static final String BASE_PACKAGE_ATTRIBUTE = "base-package";
    public static final String COMPONENT_SCAN_ELEMENT = "component-scan";


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
        } catch (IOException | DocumentException ex) {
            throw new BeansException("IOException parsing XML document from " + resource, ex);
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
    protected void doLoadBeanDefinitions(InputStream inputStream) throws DocumentException {
        SAXReader reader = new SAXReader();
        Document document = reader.read(inputStream); // 使用 SAXReader 解析输入流得到 DOM 文档对象

        Element root = document.getRootElement(); // 获取 XML 的根元素（<beans>）

        // 解析 <context:component-scan> 标签，进行包扫描，注册被 @Component 标注的类为 BeanDefinition
        Element componentScan = root.element(COMPONENT_SCAN_ELEMENT);
        if (componentScan != null) {
            String scanPath = componentScan.attributeValue(BASE_PACKAGE_ATTRIBUTE);
            if (StrUtil.isEmpty(scanPath)) {
                throw new BeansException("The value of base-package attribute can not be empty or null");
            }
            scanPackage(scanPath); // 进行包扫描
        }

        // 解析所有 <bean> 标签
        List<Element> beanList = root.elements(BEAN_ELEMENT);
        for (Element bean : beanList) {
            // 获取 bean 元素的各个属性
            String beanId = bean.attributeValue(ID_ATTRIBUTE);
            String beanName = bean.attributeValue(NAME_ATTRIBUTE);
            String className = bean.attributeValue(CLASS_ATTRIBUTE);
            String initMethodName = bean.attributeValue(INIT_METHOD_ATTRIBUTE);
            String destroyMethodName = bean.attributeValue(DESTROY_METHOD_ATTRIBUTE);
            String beanScope = bean.attributeValue(SCOPE_ATTRIBUTE);

            // 根据 className 加载对应的类
            Class<?> clazz;
            try {
                clazz = Class.forName(className);
            } catch (ClassNotFoundException e) {
                throw new BeansException("Cannot find class [" + className + "]");
            }

            // 优先使用 id，如果 id 为空则使用 name，如果都为空则用类名首字母小写作为 beanName
            beanName = StrUtil.isNotEmpty(beanId) ? beanId : beanName;
            if (StrUtil.isEmpty(beanName)) {
                beanName = StrUtil.lowerFirst(clazz.getSimpleName());
            }

            // 创建 BeanDefinition 并设置其元信息
            BeanDefinition beanDefinition = new BeanDefinition(clazz);
            beanDefinition.setInitMethodName(initMethodName);
            beanDefinition.setDestroyMethodName(destroyMethodName);
            if (StrUtil.isNotEmpty(beanScope)) {
                beanDefinition.setScope(beanScope);
            }

            // 解析 <property> 子标签，填充属性信息
            List<Element> propertyList = bean.elements(PROPERTY_ELEMENT);
            for (Element property : propertyList) {
                String propertyNameAttribute = property.attributeValue(NAME_ATTRIBUTE);
                String propertyValueAttribute = property.attributeValue(VALUE_ATTRIBUTE);
                String propertyRefAttribute = property.attributeValue(REF_ATTRIBUTE);

                if (StrUtil.isEmpty(propertyNameAttribute)) {
                    throw new BeansException("The name attribute cannot be null or empty");
                }

                // 如果 ref 存在则构造一个 BeanReference，否则直接使用 value
                Object value = propertyValueAttribute;
                if (StrUtil.isNotEmpty(propertyRefAttribute)) {
                    value = new BeanReference(propertyRefAttribute);
                }
                PropertyValue propertyValue = new PropertyValue(propertyNameAttribute, value);
                beanDefinition.getPropertyValues().addPropertyValue(propertyValue);
            }

            // 检查是否已存在相同名称的 Bean，防止重复注册
            if (getRegistry().containsBeanDefinition(beanName)) {
                throw new BeansException("Duplicate beanName[" + beanName + "] is not allowed");
            }

            // 注册当前的 BeanDefinition 到注册表中
            getRegistry().registerBeanDefinition(beanName, beanDefinition);
        }
    }


    /**
     * 扫描注解Component的类，提取信息，组装成BeanDefinition
     *
     * @param scanPath
     */
    private void scanPackage(String scanPath) {
        String[] basePackages = StrUtil.splitToArray(scanPath, ',');
        ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(getRegistry());
        scanner.doScan(basePackages);
    }
}

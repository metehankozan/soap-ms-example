package com.example.soap_example.config;

import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.config.annotation.EnableWs;
import org.springframework.ws.transport.http.MessageDispatcherServlet;
import org.springframework.ws.wsdl.wsdl11.DefaultWsdl11Definition;
import org.springframework.xml.xsd.SimpleXsdSchema;
import org.springframework.xml.xsd.XsdSchema;

@Configuration
@EnableWs
public class WebServiceConfig {
    @Bean
    public ServletRegistrationBean<MessageDispatcherServlet> messageDispatcherServlet(ApplicationContext ctx) {
        var servlet = new MessageDispatcherServlet();
        servlet.setApplicationContext(ctx);
        servlet.setTransformWsdlLocations(true);
        return new ServletRegistrationBean<>(servlet, "/ws/*");
    }

    @Bean(name = "customers")
    public DefaultWsdl11Definition defaultWsdl11Definition(XsdSchema customerSchema) {
        var wsdl = new DefaultWsdl11Definition();
        wsdl.setPortTypeName("CustomerPort");
        wsdl.setLocationUri("/ws");
        wsdl.setTargetNamespace("http://example.com/customer");
        wsdl.setSchema(customerSchema);
        return wsdl;
    }

    @Bean
    public XsdSchema customerSchema() {
        return new SimpleXsdSchema(new ClassPathResource("customer.xsd"));
    }

    /**
     * Registers a JAXB2 marshaller/unmarshaller for your generated classes.
     */
    @Bean
    public Jaxb2Marshaller jaxb2Marshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        // this must match the package of your generated JAXB classes
        marshaller.setContextPath("com.example.customer");
        return marshaller;
    }
}


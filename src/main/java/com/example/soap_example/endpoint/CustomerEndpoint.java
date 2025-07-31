package com.example.soap_example.endpoint;

import com.example.customer.*;
import com.example.soap_example.entity.Customer;
import com.example.soap_example.repository.CustomerRepository;
import jakarta.xml.bind.JAXBElement;
import org.springframework.oxm.XmlMappingException;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import org.springframework.ws.soap.SoapHeaderElement;
import org.springframework.ws.soap.server.endpoint.annotation.SoapHeader;

import java.io.IOException;

@Endpoint
public class CustomerEndpoint {
    private static final String NAMESPACE = "http://example.com/customer";
    private final CustomerRepository repo;
    private final Jaxb2Marshaller jaxb2Marshaller;

    public CustomerEndpoint(CustomerRepository repo, Jaxb2Marshaller jaxb2Marshaller) {
        this.repo = repo;
        this.jaxb2Marshaller = jaxb2Marshaller;
    }

    @PayloadRoot(namespace = NAMESPACE, localPart = "SaveCustomerRequest")
    @ResponsePayload
    public SaveCustomerResponse saveCustomer(@RequestPayload SaveCustomerRequest req) {
        Customer customer = new Customer();
        customer.setName(req.getName());
        customer.setEmail(req.getEmail());
        customer = repo.save(customer);

        var resp = new SaveCustomerResponse();
        resp.setId(customer.getId());
        resp.setStatus("OK");
        return resp;
    }

    @PayloadRoot(namespace = NAMESPACE, localPart = "ListCustomersRequest")
    @ResponsePayload
    public ListCustomersResponse listCustomers(@RequestPayload ListCustomersRequest req) {
        ListCustomersResponse resp = new ListCustomersResponse();

        // Map each JPA Customer to the JAXB Customer type
        repo.findAll().forEach(entity -> {
            com.example.customer.Customer jaxbCust = new com.example.customer.Customer();
            jaxbCust.setId(entity.getId());
            jaxbCust.setName(entity.getName());
            jaxbCust.setEmail(entity.getEmail());
            resp.getCustomer().add(jaxbCust);
        });

        return resp;
    }

    @PayloadRoot(namespace = NAMESPACE, localPart = "GetCustomerRequest")
    @ResponsePayload
    public GetCustomerResponse getCustomer(
            @RequestPayload GetCustomerRequest request,
            @SoapHeader("{http://example.com/customer}CustomerIdHeader")
            SoapHeaderElement headerElement) throws XmlMappingException, IOException {

        // Now manually unmarshal the header into your JAXB class:
        CustomerIdHeader cid = (CustomerIdHeader) jaxb2Marshaller
                .unmarshal(headerElement.getSource());

        long id = cid.getId();
        GetCustomerResponse resp = new GetCustomerResponse();

        repo.findById(id).ifPresent(entity -> {
            com.example.customer.Customer jaxbCust = new com.example.customer.Customer();
            jaxbCust.setId(entity.getId());
            jaxbCust.setName(entity.getName());
            jaxbCust.setEmail(entity.getEmail());
            resp.setCustomer(jaxbCust);
        });

        return resp;
    }
}

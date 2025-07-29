package com.example.soap_example.endpoint;

import com.example.customer.ListCustomersRequest;
import com.example.customer.ListCustomersResponse;
import com.example.customer.SaveCustomerRequest;
import com.example.customer.SaveCustomerResponse;
import com.example.soap_example.entity.Customer;
import com.example.soap_example.repository.CustomerRepository;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Endpoint
public class CustomerEndpoint {
    private static final String NAMESPACE = "http://example.com/customer";
    private final CustomerRepository repo;

    public CustomerEndpoint(CustomerRepository repo) { this.repo = repo; }

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
}

package org.application.spring.ddd.service;

import org.application.spring.ddd.model.entity.Customer;
import org.application.spring.ddd.repository.CustomerRepository;
import org.springframework.stereotype.Service;

@Service
public class CustomerService extends AppService<Customer, String, CustomerRepository> implements CustomerRepository {

    public CustomerService(CustomerRepository customerRepository) {
        super(customerRepository);
    }

}

package org.application.spring.ddd.service;

import org.application.spring.ddd.model.entity.Customer;
import org.application.spring.ddd.repository.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerService extends AppService<Customer, String, CustomerRepository> implements CustomerRepository {

    public CustomerService(CustomerRepository customerRepository) {
        super(customerRepository);
    }

    @Transactional(transactionManager = "appTM")
    @Override
    public int deleteById(List<String> ids) {
        if (Optional.ofNullable(ids).isPresent()) {
            return repository.deleteById(ids);
        }
        return 0;
    }

    @Transactional(transactionManager = "appTM")
    public int deleteById(String... ids) {
        if (ids.length > 0) {
            return repository.deleteById(List.of(ids));
        }
        return 0;
    }
}

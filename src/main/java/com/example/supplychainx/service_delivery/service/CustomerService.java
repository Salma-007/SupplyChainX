package com.example.supplychainx.service_delivery.service;

import com.example.supplychainx.service_approvisionnement.exceptions.BusinessException;
import com.example.supplychainx.service_delivery.dto.customer.CustomerRequestDTO;
import com.example.supplychainx.service_delivery.dto.customer.CustomerResponseDTO;
import com.example.supplychainx.service_delivery.exceptions.CustomerNotFoundException;
import com.example.supplychainx.service_delivery.mapper.CustomerMapper;
import com.example.supplychainx.service_delivery.model.Customer;
import com.example.supplychainx.service_delivery.repository.CustomerRepository;
import com.example.supplychainx.service_delivery.repository.OrderRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class CustomerService {

    private final CustomerMapper customerMapper;
    private final CustomerRepository customerRepository;
    private final OrderRepository orderRepository;

    public CustomerResponseDTO createCustomer(CustomerRequestDTO dto){
        Customer customer = customerMapper.toEntity(dto);
        Customer saved = customerRepository.save(customer);
        return customerMapper.toResponseDto(saved);
    }

    public List<CustomerResponseDTO> getAllCustomers(){
        List<Customer> customers = customerRepository.findAll();
        return customers.stream()
                .map(customerMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    public void deleteCustomer(Long id){
        if(!customerRepository.existsById(id)){
            throw new CustomerNotFoundException("Customer not found with id :"+id);
        }

        if(orderRepository.existsByCustomer_Id(id)){
            throw new BusinessException("Impossible de supprimer le client. Il est utilisé dans une ou plusieurs commandes.");
        }

        customerRepository.deleteById(id);
    }

    public CustomerResponseDTO getCustomerById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found with id: " + id));
        return customerMapper.toResponseDto(customer);
    }
    @Transactional
    public CustomerResponseDTO updateCustomer(Long id, CustomerRequestDTO dto) {
        Customer existingCustomer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found with id: " + id));

        customerMapper.updateCustomerFromDto(dto, existingCustomer);
        Customer updatedCustomer = customerRepository.save(existingCustomer);
        return customerMapper.toResponseDto(updatedCustomer);
    }

    public CustomerResponseDTO getCustomerByName(String name){
        Customer customer = customerRepository.findByNameIgnoreCase(name)
                .orElseThrow(() -> new CustomerNotFoundException("Client non trouvé avec le nom : " + name));

        return customerMapper.toResponseDto(customer);
    }

}

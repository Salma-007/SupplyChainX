package com.example.supplychainx.test_delivery;

import com.example.supplychainx.service_approvisionnement.exceptions.BusinessException;
import com.example.supplychainx.service_delivery.dto.customer.CustomerRequestDTO;
import com.example.supplychainx.service_delivery.dto.customer.CustomerResponseDTO;
import com.example.supplychainx.service_delivery.exceptions.CustomerNotFoundException;
import com.example.supplychainx.service_delivery.mapper.CustomerMapper;
import com.example.supplychainx.service_delivery.model.Customer;
import com.example.supplychainx.service_delivery.repository.CustomerRepository;
import com.example.supplychainx.service_delivery.repository.OrderRepository;
import com.example.supplychainx.service_delivery.service.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerMapper customerMapper;
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private CustomerService customerService;

    private CustomerRequestDTO customerRequestDTO;
    private Customer customer;
    private CustomerResponseDTO customerResponseDTO;
    private final Long CUSTOMER_ID = 1L;
    private final String CUSTOMER_NAME = "John Doe";

    @BeforeEach
    void setUp() {
        customerRequestDTO = new CustomerRequestDTO(CUSTOMER_NAME, "123 Main St", "New York");

        customer = new Customer();
        customer.setId(CUSTOMER_ID);
        customer.setName(CUSTOMER_NAME);
        customer.setAddress("123 Main St");

        customerResponseDTO = new CustomerResponseDTO(CUSTOMER_ID, CUSTOMER_NAME, "123 Main St", "New York", null);
    }

    @Test
    void createCustomer_Success() {
        when(customerMapper.toEntity(customerRequestDTO)).thenReturn(customer);
        when(customerRepository.save(customer)).thenReturn(customer);
        when(customerMapper.toResponseDto(customer)).thenReturn(customerResponseDTO);

        CustomerResponseDTO result = customerService.createCustomer(customerRequestDTO);

        assertNotNull(result);
        assertEquals(CUSTOMER_ID, result.getId());
        verify(customerRepository).save(customer);
    }

    // --- Tests pour getAllCustomers ---
    @Test
    void getAllCustomers_Success() {
        List<Customer> customers = Arrays.asList(customer, new Customer());
        when(customerRepository.findAll()).thenReturn(customers);
        when(customerMapper.toResponseDto(any(Customer.class)))
                .thenReturn(customerResponseDTO)
                .thenReturn(new CustomerResponseDTO());

        List<CustomerResponseDTO> result = customerService.getAllCustomers();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(customerRepository).findAll();
    }

    // --- Tests pour getCustomerById ---
    @Test
    void getCustomerById_Success() {
        when(customerRepository.findById(CUSTOMER_ID)).thenReturn(Optional.of(customer));
        when(customerMapper.toResponseDto(customer)).thenReturn(customerResponseDTO);

        CustomerResponseDTO result = customerService.getCustomerById(CUSTOMER_ID);

        assertNotNull(result);
        assertEquals(CUSTOMER_ID, result.getId());
    }

    @Test
    void getCustomerById_ThrowsCustomerNotFoundException() {
        when(customerRepository.findById(CUSTOMER_ID)).thenReturn(Optional.empty());

        assertThrows(CustomerNotFoundException.class, () -> customerService.getCustomerById(CUSTOMER_ID));
    }

    // --- Tests pour deleteCustomer ---
    @Test
    void deleteCustomer_Success() {
        when(customerRepository.existsById(CUSTOMER_ID)).thenReturn(true);
        when(orderRepository.existsByCustomer_Id(CUSTOMER_ID)).thenReturn(false);

        customerService.deleteCustomer(CUSTOMER_ID);

        verify(customerRepository).existsById(CUSTOMER_ID);
        verify(orderRepository).existsByCustomer_Id(CUSTOMER_ID);
        verify(customerRepository).deleteById(CUSTOMER_ID);
    }

    @Test
    void deleteCustomer_ThrowsCustomerNotFoundException() {
        when(customerRepository.existsById(CUSTOMER_ID)).thenReturn(false);

        assertThrows(CustomerNotFoundException.class, () -> customerService.deleteCustomer(CUSTOMER_ID));
        verify(customerRepository, never()).deleteById(anyLong());
        verify(orderRepository, never()).existsByCustomer_Id(anyLong());
    }

    @Test
    void deleteCustomer_ThrowsBusinessException_OrdersExist() {
        when(customerRepository.existsById(CUSTOMER_ID)).thenReturn(true);
        when(orderRepository.existsByCustomer_Id(CUSTOMER_ID)).thenReturn(true); // Commande associée

        assertThrows(BusinessException.class, () -> customerService.deleteCustomer(CUSTOMER_ID));
        verify(customerRepository, never()).deleteById(anyLong());
    }

    @Test
    void updateCustomer_Success() {
        CustomerRequestDTO newDto = new CustomerRequestDTO("Jane Smith", "456 New Road", "New York");
        Customer updatedCustomer = new Customer(CUSTOMER_ID,"Jane Smith", "Jane Smith", "456 New Road", null);
        CustomerResponseDTO expectedResponse = new CustomerResponseDTO(CUSTOMER_ID,"Jane Smith", "Jane Smith", "456 New Road", null);

        when(customerRepository.findById(CUSTOMER_ID)).thenReturn(Optional.of(customer));
        doNothing().when(customerMapper).updateCustomerFromDto(newDto, customer);
        when(customerRepository.save(customer)).thenReturn(updatedCustomer);
        when(customerMapper.toResponseDto(updatedCustomer)).thenReturn(expectedResponse);

        CustomerResponseDTO result = customerService.updateCustomer(CUSTOMER_ID, newDto);

        assertNotNull(result);
        assertEquals("Jane Smith", result.getName());
        verify(customerMapper).updateCustomerFromDto(newDto, customer);
        verify(customerRepository).save(customer);
    }

    @Test
    void updateCustomer_ThrowsCustomerNotFoundException() {
        when(customerRepository.findById(CUSTOMER_ID)).thenReturn(Optional.empty());

        assertThrows(CustomerNotFoundException.class, () -> customerService.updateCustomer(CUSTOMER_ID, customerRequestDTO));
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void getCustomerByName_Success() {
        when(customerRepository.findByNameIgnoreCase(CUSTOMER_NAME)).thenReturn(Optional.of(customer));
        when(customerMapper.toResponseDto(customer)).thenReturn(customerResponseDTO);

        CustomerResponseDTO result = customerService.getCustomerByName(CUSTOMER_NAME);

        assertNotNull(result);
        assertEquals(CUSTOMER_NAME, result.getName());
        verify(customerRepository).findByNameIgnoreCase(CUSTOMER_NAME);
    }

    @Test
    void getCustomerByName_ThrowsCustomerNotFoundException() {
        String nonExistentName = "Non Existent";
        when(customerRepository.findByNameIgnoreCase(nonExistentName)).thenReturn(Optional.empty());

        assertThrows(CustomerNotFoundException.class, () -> customerService.getCustomerByName(nonExistentName));
    }
}
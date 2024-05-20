package io.brightskies.loyalty.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import io.brightskies.loyalty.customer.entity.Customer;
import io.brightskies.loyalty.customer.service.CustomerService;
import io.brightskies.loyalty.pointsEntry.entity.PointsEntry;
import io.brightskies.loyalty.pointsEntry.exception.PointsEntryException;
import io.brightskies.loyalty.pointsEntry.exception.PointsEntryExceptionMessages;
import io.brightskies.loyalty.pointsEntry.repo.PointsEntryRepo;
import io.brightskies.loyalty.pointsEntry.service.PointsEntryService;
import io.brightskies.loyalty.pointsEntry.service.PointsEntryServiceImpl;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
public class PointsEntryServiceTest {
    @TestConfiguration
    static class ServiceTestConifg {
        @Bean
        @Autowired
        PointsEntryService service(PointsEntryRepo pointsEntryRepo, CustomerService customerService) {
            return new PointsEntryServiceImpl(pointsEntryRepo, customerService);
        }
    }

    @MockBean
    private PointsEntryRepo pointsEntryRepo;

    @MockBean
    private CustomerService customerService;

    @Autowired
    private PointsEntryService pointsEntryService;

    private static Customer customer;

    private static PointsEntry pointsEntry;

    private static List<PointsEntry> pointsEntries;

    @BeforeAll
    public static void setUp() {
        customer = new Customer(1, null, 0);
        pointsEntry = new PointsEntry(1, 50, Date.valueOf("2030-04-20"), customer, false);

        pointsEntries = new ArrayList<PointsEntry>();
        pointsEntries.add(pointsEntry);
    }

    @BeforeEach
    public void setUpMocks() {
        when(customerService.getCustomer(customer.getPhoneNumber())).thenReturn(customer);

        when(pointsEntryRepo.save(pointsEntry)).thenReturn(pointsEntry);

        when(pointsEntryRepo.findById(pointsEntry.getId())).thenReturn(Optional.of(pointsEntry));
        when(pointsEntryRepo.findById(pointsEntry.getId() - 1)).thenReturn(Optional.empty());

        when(pointsEntryRepo.findAll()).thenReturn(pointsEntries);
        when(pointsEntryRepo.findNonExpiredPointsEntriesByCustomer(any(Customer.class), any(Date.class)))
                .thenReturn(pointsEntries);

        when(pointsEntryRepo.findByExpiryDateBetweenDatesByCustomer(any(Customer.class), any(Date.class),
                any(Date.class)))
                .thenReturn(new ArrayList<PointsEntry>());

        when(pointsEntryRepo.findByExpiryDateBefore(any(Date.class))).thenReturn(pointsEntries);
    }

    @Test
    public void createPointsEntry_ReturnsSavedPointsEntry() {
        assertEquals(pointsEntry, pointsEntryService.createPointsEntry(pointsEntry));
    }

    @Test
    public void getPointsEntry_RetrievesPointsEntryWhenGivenValidId() {
        assertEquals(pointsEntry, pointsEntryService.getPointsEntry(pointsEntry.getId()));
    }

    @Test
    public void getPointsEntry_ThrowsErrorWhenGivenInvalidId() {
        PointsEntryException ex = assertThrows(PointsEntryException.class,
                () -> {
                    pointsEntryService.getPointsEntry(pointsEntry.getId() - 1);
                });

        assertTrue(ex.getMessage().contains(PointsEntryExceptionMessages.POINT_ENTRY_NOT_FOUND));
    }

    @Test
    public void getAllPointsEntries_ReturnsAListOfPointsEntries() {
        assertEquals(pointsEntries, pointsEntryService.getAllPointsEntries());
    }

    @Test
    public void getPointsEntriesByCustomer_ReturnsAListOfPointEntriesGivenCustomer() {
        assertEquals(pointsEntries, pointsEntryService.getNonExpiredPointsEntriesByCustomer(customer));
    }

    @Test
    public void updatePointsInEntry_ReturnsUpdatedPointEntryWhenGivenValidId() {
        PointsEntry expectedPointsEntry = new PointsEntry(pointsEntry.getId(), pointsEntry.getNumOfPoints() - 10,
                pointsEntry.getExpiryDate(), customer, false);
        when(pointsEntryRepo.save(expectedPointsEntry)).thenReturn(expectedPointsEntry);

        assertEquals(expectedPointsEntry,
                pointsEntryService.updatePointsInEntry(pointsEntry.getId(), pointsEntry.getNumOfPoints() - 10));
    }

    @Test
    public void updatePointsInEntry_ThrowsErrorWhenGivenInvalidId() {
        PointsEntryException ex = assertThrows(PointsEntryException.class,
                () -> {
                    pointsEntryService.updatePointsInEntry(pointsEntry.getId() - 1, 20);
                });

        assertTrue(ex.getMessage().contains(PointsEntryExceptionMessages.POINT_ENTRY_NOT_FOUND));

        verify(pointsEntryRepo, times(0)).save(any(PointsEntry.class));
    }

    @Test
    public void getSoonToExpirePointsEntries_ReturnsEmptyListWhenNothingExpiresSoon() {
        List<PointsEntry> pointsEntries = pointsEntryService.getSoonToExpirePointsEntries(customer.getPhoneNumber());

        assertEquals(0, pointsEntries.size());
    }

    @Test
    public void deletePointsEntry_CallsDeleteWhenGivenValidId() {
        pointsEntryService.deletePointsEntry(pointsEntry.getId());

        verify(pointsEntryRepo, times(1)).deleteById(pointsEntry.getId());
    }

    @Test
    public void setExpiredPointsEntries_SetsExpiredToTrue() {
        pointsEntryService.setExpiredPointsEntries();

        assertTrue(pointsEntry.isExpired());
        verify(pointsEntryRepo, times(1)).save(pointsEntry);
    }

    @Test
    public void deletePointsEntry_ThrowsErrorWhenGivenInvalidId() {
        PointsEntryException ex = assertThrows(PointsEntryException.class,
                () -> {
                    pointsEntryService.deletePointsEntry(pointsEntry.getId() - 1);
                });

        assertTrue(ex.getMessage().contains(PointsEntryExceptionMessages.POINT_ENTRY_NOT_FOUND));

        verify(pointsEntryRepo, times(0)).deleteById(anyLong());

    }
}

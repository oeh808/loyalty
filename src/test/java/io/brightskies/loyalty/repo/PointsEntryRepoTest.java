package io.brightskies.loyalty.repo;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import io.brightskies.loyalty.customer.entity.Customer;
import io.brightskies.loyalty.customer.repo.CustomerRepo;
import io.brightskies.loyalty.pointsEntry.entity.PointsEntry;
import io.brightskies.loyalty.pointsEntry.repo.PointsEntryRepo;

@ActiveProfiles("test")
@DataJpaTest
public class PointsEntryRepoTest {
    @Autowired
    private CustomerRepo customerRepo;

    @Autowired
    private PointsEntryRepo pointsEntryRepo;

    private static Customer customer;

    private static PointsEntry pointsEntry1;
    private static PointsEntry pointsEntry2;
    private static PointsEntry pointsEntry3;
    private static PointsEntry pointsEntry4;

    private static long today;

    @BeforeAll
    public static void setUp() {
        customer = new Customer(0, "0291303192", 200);

        // Expiration dates hierarchy from first to expire to last to expire:
        // pointsEntry1 -> pointsEntry3 -> pointsEntry2
        pointsEntry1 = new PointsEntry(0, 150, Date.valueOf("2030-02-17"), null, false);
        pointsEntry2 = new PointsEntry(0, 50, Date.valueOf("2030-04-17"), null, false);
        pointsEntry3 = new PointsEntry(0, 50, Date.valueOf("2030-03-17"), null, false);

        // Expired pointsEntry
        pointsEntry4 = new PointsEntry(0, 50, Date.valueOf("2010-03-17"), null, true);

        today = Calendar.getInstance().getTimeInMillis();
    }

    @BeforeEach
    public void setUpForEach() {
        customer = customerRepo.save(customer);

        pointsEntry1.setCustomer(customer);
        pointsEntry1 = pointsEntryRepo.save(pointsEntry1);

        pointsEntry2.setCustomer(customer);
        pointsEntry2 = pointsEntryRepo.save(pointsEntry2);

        pointsEntry3.setCustomer(customer);
        pointsEntry3 = pointsEntryRepo.save(pointsEntry3);

        pointsEntry4.setCustomer(customer);
        pointsEntry4 = pointsEntryRepo.save(pointsEntry4);
    }

    @AfterEach
    public void tearDownForEach() {
        pointsEntryRepo.deleteAll();
        customerRepo.deleteAll();
    }

    @Test
    public void findNonExpiredPointsEntriesByCustomer_ReturnsListOfNonExpiredPointEntriesInAscendingOrder() {
        List<PointsEntry> sortedPointsEntries = new ArrayList<>();
        sortedPointsEntries.add(pointsEntry1);
        sortedPointsEntries.add(pointsEntry3);
        sortedPointsEntries.add(pointsEntry2);

        assertEquals(sortedPointsEntries,
                pointsEntryRepo.findNonExpiredPointsEntriesByCustomer(customer, new Date(today)));
    }

    @Test
    public void findByExpiryDateBetweenOrderByExpiryDateAsc_ReturnsListOfExpiryDatesBetweenTwoDates() {
        List<PointsEntry> sortedPointsEntries = new ArrayList<>();
        sortedPointsEntries.add(pointsEntry3);
        sortedPointsEntries.add(pointsEntry2);

        assertEquals(sortedPointsEntries,
                pointsEntryRepo.findByExpiryDateBetweenOrderByExpiryDateAsc(pointsEntry3.getExpiryDate(),
                        pointsEntry2.getExpiryDate()));
    }
}

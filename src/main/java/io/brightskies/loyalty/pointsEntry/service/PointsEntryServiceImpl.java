package io.brightskies.loyalty.pointsEntry.service;

import java.sql.Date;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.time.DateUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import io.brightskies.loyalty.customer.entity.Customer;
import io.brightskies.loyalty.customer.service.CustomerService;
import io.brightskies.loyalty.pointsEntry.entity.PointsEntry;
import io.brightskies.loyalty.pointsEntry.exception.PointsEntryException;
import io.brightskies.loyalty.pointsEntry.exception.PointsEntryExceptionMessages;
import io.brightskies.loyalty.pointsEntry.repo.PointsEntryRepo;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class PointsEntryServiceImpl implements PointsEntryService {
    private PointsEntryRepo pointsEntryRepo;
    private CustomerService customerService;

    public PointsEntryServiceImpl(PointsEntryRepo pointsEntryRepo, CustomerService customerService) {
        this.pointsEntryRepo = pointsEntryRepo;
        this.customerService = customerService;
    }

    @Override
    public PointsEntry createPointsEntry(PointsEntry pointsEntry) {
        log.info("Running createPointsEntry(" + pointsEntry.toString() + ") in PointsEntryServiceImpl...");
        return pointsEntryRepo.save(pointsEntry);
    }

    @Override
    public PointsEntry getPointsEntry(long id) {
        log.info("Running getPointsEntry(" + id + ") in PointsEntryServiceImpl...");
        Optional<PointsEntry> opPointsEntry = pointsEntryRepo.findById(id);

        if (opPointsEntry.isPresent()) {
            return opPointsEntry.get();
        } else {
            log.error("Invalid points entry id: " + id + "!");
            throw new PointsEntryException(PointsEntryExceptionMessages.POINT_ENTRY_NOT_FOUND);
        }
    }

    @Override
    public List<PointsEntry> getAllPointsEntries() {
        log.info("Running getAllPointsEntries() in PointsEntryServiceImpl...");
        return pointsEntryRepo.findAll();
    }

    @Override
    public List<PointsEntry> getNonExpiredPointsEntriesByCustomer(Customer customer) {
        log.info("Running getNonExpiredPointsEntriesByCustomer(" + customer.toString()
                + ") in PointsEntryServiceImpl...");

        long today = Calendar.getInstance().getTimeInMillis();
        Date yesterday = new Date(today);
        yesterday = new Date(DateUtils.addDays(yesterday, -1).getTime());

        log.info("Retrieving point entries set to expire before: " + yesterday.toString() + "...");

        return pointsEntryRepo.findNonExpiredPointsEntriesByCustomer(customer, yesterday);
    }

    @Override
    public PointsEntry updatePointsInEntry(long id, int points) {
        log.info("Running updatePointsInEntry(" + id + ", " + points + ") in PointsEntryServiceImpl...");
        log.info("Checking points entry id exists...");
        Optional<PointsEntry> opPointsEntry = pointsEntryRepo.findById(id);

        if (opPointsEntry.isEmpty()) {
            log.error("Invalid points entry id: " + id + "!");
            throw new PointsEntryException(PointsEntryExceptionMessages.POINT_ENTRY_NOT_FOUND);
        } else {
            log.info("Updating points entry...");
            PointsEntry pointsEntry = opPointsEntry.get();
            pointsEntry.setNumOfPoints(points);

            log.info("Saving updated points entry...");
            return pointsEntryRepo.save(pointsEntry);
        }
    }

    @Override
    public List<PointsEntry> getSoonToExpirePointsEntries(String phoneNumber) {
        log.info("Running getSoonToExpirePointsEntries(" + phoneNumber + ") in PointsEntryServiceImpl...");
        Customer customer = customerService.getCustomer(phoneNumber);

        long today = Calendar.getInstance().getTimeInMillis();
        Date todayDate = new Date(today);
        // FIXME: Figure out what an appropriate numbers of time to add would be
        Date laterDate = new Date(DateUtils.addMonths(todayDate, 1).getTime());
        log.info("Retrieving points entries between: " + todayDate.toString() + " and " + laterDate.toString() + "...");

        return pointsEntryRepo.findByExpiryDateBetweenDatesByCustomer(customer, todayDate, laterDate);
    }

    @Override
    @Scheduled(cron = "0 0 0 * * *")
    public void setExpiredPointsEntries() {
        log.info("Running setExpiredPointsEntries() in PointsEntryServiceImpl...");
        long today = Calendar.getInstance().getTimeInMillis();
        Date todayDate = new Date(today);

        log.info("Retrieving points entries before: " + todayDate.toString() + "...");
        List<PointsEntry> expiredPointsEntries = pointsEntryRepo.findByExpiryDateBefore(todayDate);

        log.info("Setting points entries to expired...");
        for (PointsEntry pointsEntry : expiredPointsEntries) {
            log.info("Setting points entry: " + pointsEntry.toString() + " to expired...");
            pointsEntry.setExpired(true);
            pointsEntryRepo.save(pointsEntry);
        }
    }

    @Override
    public void deletePointsEntry(long id) {
        log.info("Running getPointsEntry(" + id + ") in PointsEntryServiceImpl...");
        log.info("Checking points entry id exists...");
        Optional<PointsEntry> opPointsEntry = pointsEntryRepo.findById(id);

        if (opPointsEntry.isPresent()) {
            log.info("Deleting points entry...");
            pointsEntryRepo.deleteById(id);
        } else {
            log.error("Invalid points entry id: " + id + "!");
            throw new PointsEntryException(PointsEntryExceptionMessages.POINT_ENTRY_NOT_FOUND);
        }
    }
}

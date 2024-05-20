package io.brightskies.loyalty.pointsEntry.service;

import java.sql.Date;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Service;

import io.brightskies.loyalty.customer.entity.Customer;
import io.brightskies.loyalty.pointsEntry.entity.PointsEntry;
import io.brightskies.loyalty.pointsEntry.exception.PointsEntryException;
import io.brightskies.loyalty.pointsEntry.exception.PointsEntryExceptionMessages;
import io.brightskies.loyalty.pointsEntry.repo.PointsEntryRepo;

@Service
public class PointsEntryServiceImpl implements PointsEntryService {
    private PointsEntryRepo pointsEntryRepo;

    public PointsEntryServiceImpl(PointsEntryRepo pointsEntryRepo) {
        this.pointsEntryRepo = pointsEntryRepo;
    }

    @Override
    public PointsEntry createPointsEntry(PointsEntry pointsEntry) {
        return pointsEntryRepo.save(pointsEntry);
    }

    @Override
    public PointsEntry getPointsEntry(long id) {
        Optional<PointsEntry> opPointsEntry = pointsEntryRepo.findById(id);

        if (opPointsEntry.isPresent()) {
            return opPointsEntry.get();
        } else {
            throw new PointsEntryException(PointsEntryExceptionMessages.POINT_ENTRY_NOT_FOUND);
        }
    }

    @Override
    public List<PointsEntry> getAllPointsEntries() {
        return pointsEntryRepo.findAll();
    }

    @Override
    public List<PointsEntry> getNonExpiredPointsEntriesByCustomer(Customer customer) {
        long today = Calendar.getInstance().getTimeInMillis();
        Date yesterday = new Date(today);
        yesterday = new Date(DateUtils.addDays(yesterday, -1).getTime());

        return pointsEntryRepo.findNonExpiredPointsEntriesByCustomer(customer, yesterday);
    }

    @Override
    public PointsEntry updatePointsInEntry(long id, int points) {
        Optional<PointsEntry> opPointsEntry = pointsEntryRepo.findById(id);

        if (opPointsEntry.isEmpty()) {
            throw new PointsEntryException(PointsEntryExceptionMessages.POINT_ENTRY_NOT_FOUND);
        } else {
            PointsEntry pointsEntry = opPointsEntry.get();
            pointsEntry.setNumOfPoints(points);

            return pointsEntryRepo.save(pointsEntry);
        }
    }

    @Override
    public List<PointsEntry> getSoonToExpirePointsEntries(Customer customer) {
        long today = Calendar.getInstance().getTimeInMillis();
        Date todayDate = new Date(today);
        // FIXME: Figure out what an appropriate numbers of time to add would be
        Date laterDate = new Date(DateUtils.addMonths(todayDate, 1).getTime());

        return pointsEntryRepo.findByExpiryDateBetweenDatesByCustomer(customer, todayDate, laterDate);
    }

    @Override
    public void deletePointsEntry(long id) {
        Optional<PointsEntry> opPointsEntry = pointsEntryRepo.findById(id);

        if (opPointsEntry.isPresent()) {
            pointsEntryRepo.deleteById(id);
        } else {
            throw new PointsEntryException(PointsEntryExceptionMessages.POINT_ENTRY_NOT_FOUND);
        }
    }
}

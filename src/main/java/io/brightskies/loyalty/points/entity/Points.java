package io.brightskies.loyalty.points.entity;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Points")
public class Points {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private int numOfPoints;
    private Date expiryDate;
}

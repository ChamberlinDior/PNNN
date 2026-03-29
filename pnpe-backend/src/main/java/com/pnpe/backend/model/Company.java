package com.pnpe.backend.model;

import com.pnpe.backend.model.enums.CompanyStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "companies")
public class Company extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String name;

    private String sector;
    private String city;
    private String address;
    private String contactName;
    private String contactEmail;
    private String contactPhone;
    private String partnershipNotes;

    @Enumerated(EnumType.STRING)
    private CompanyStatus status = CompanyStatus.PROSPECT;
}

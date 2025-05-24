<<<<<<< HEAD
package com.nexbus.nexbus_backend.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "bus_amenities")
@Data
public class BusAmenities {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer busAmenityId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bus_id", nullable = false)
    private Bus bus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "amenity_id", nullable = false)
    private Amenity amenity;
=======
package com.nexbus.nexbus_backend.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "bus_amenities")
@Data
public class BusAmenities {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer busAmenityId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bus_id", nullable = false)
    private Bus bus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "amenity_id", nullable = false)
    private Amenity amenity;
>>>>>>> 44bd435102e963e84bc2fef038ba51696f12ca66
}
package com.dailycode.hotelBooking.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.time.LocalDate;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookedRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookingId;

    @Column(name = "check_in")
    private LocalDate checkInDate;

    @Column(name = "check_out")
    private LocalDate checkOutDate;

    @Column(name = "guest_name")
    private String guestName;

    @Column(name = "guest_email")
    private String guestEmail;

    @Column(name = "adults")
    private int numberOfAdults;

    @Column(name = "children")
    private int numberOfChildren;

    @Column(name = "total_guests")
    private int totalNumOfGuest;

    @Column(name = "confirmation_code")
    private String bookingConfirmationCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private Room room;

    public void calculateNumOfTotalGuest() {
        this.totalNumOfGuest = this.numberOfAdults + this.numberOfChildren;
    }

    public void setNumberOfAdults(int numberOfAdults) {
        this.numberOfAdults = numberOfAdults;
        calculateNumOfTotalGuest();
    }

    public void setNumberOfChildren(int numberOfChildren) {
        this.numberOfChildren = numberOfChildren;
        calculateNumOfTotalGuest();
    }

    public void setBookingConfirmationCode(String bookingConfirmationCode) {
        this.bookingConfirmationCode = bookingConfirmationCode;
    }
}

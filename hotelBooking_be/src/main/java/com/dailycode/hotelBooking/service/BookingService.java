package com.dailycode.hotelBooking.service;

import com.dailycode.hotelBooking.model.BookedRoom;

import java.util.List;

public interface BookingService {
    List<BookedRoom> getAllBookingsByRoomId(Long id);
    List<BookedRoom> getAllBookings();

    BookedRoom findByBookingConfirmationCode(String confirmationCode);

    String saveBooking(Long roomId, BookedRoom bookingRequest);

    void cancelBooking(Long bookingId);
}

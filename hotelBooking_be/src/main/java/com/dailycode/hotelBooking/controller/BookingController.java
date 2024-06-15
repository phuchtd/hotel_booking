package com.dailycode.hotelBooking.controller;

import com.dailycode.hotelBooking.exception.InvalidBookingRequestException;
import com.dailycode.hotelBooking.exception.ResourceNotFoundException;
import com.dailycode.hotelBooking.model.BookedRoom;
import com.dailycode.hotelBooking.model.Room;
import com.dailycode.hotelBooking.response.BookingResponse;
import com.dailycode.hotelBooking.response.RoomResponse;
import com.dailycode.hotelBooking.service.BookingService;
import com.dailycode.hotelBooking.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/bookings")
@CrossOrigin(origins = "*")
public class BookingController {

    private final BookingService bookingService;
    private final RoomService roomService;

    @GetMapping("all-bookings")
    public ResponseEntity<List<BookingResponse>> getAllBookings() {
        List<BookedRoom> bookings = bookingService.getAllBookings();
        List<BookingResponse> bookingResponses = new ArrayList<>();
        for (BookedRoom booking: bookings) {
            BookingResponse bookingResponse = getBookingResponse(booking);
            bookingResponses.add(bookingResponse);
        }
        return ResponseEntity.ok(bookingResponses);
    }


    @GetMapping("/confirmation/{confirmationCode}")
    public ResponseEntity<?> getBookingByConfirmationCode(@PathVariable String confirmationCode) {
        try {
            BookedRoom booking = bookingService.findByBookingConfirmationCode(confirmationCode);
            BookingResponse bookingResponse = getBookingResponse(booking);
            return ResponseEntity.ok(bookingResponse);
        } catch (ResourceNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }

    @PostMapping("/room/{roomId}/booking")
    public ResponseEntity<?> saveBooking(@PathVariable Long roomId, @RequestBody BookedRoom bookingRequest) {
        try {
            String confirmationCode = bookingService.saveBooking(roomId, bookingRequest);
            return ResponseEntity.ok("Room booked successfully. Your booking confirmation is " + confirmationCode);
        } catch (InvalidBookingRequestException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/booking/{bookingId}/delete")
    public void cancelBooking(@PathVariable Long bookingId) {
        bookingService.cancelBooking(bookingId);
    }

    private BookingResponse getBookingResponse(BookedRoom booking) {
        Room  room = roomService.getRoomById(booking.getRoom().getId()).get();
        RoomResponse roomResponse = new RoomResponse(room.getId(), room.getRoomType(), room.getRoomPrice());
        return new BookingResponse(
                booking.getBookingId(), booking.getCheckInDate(),
                booking.getCheckOutDate(), booking.getGuestName(),
                booking.getGuestEmail(), booking.getNumberOfAdults(),
                booking.getNumberOfChildren(), booking.getTotalNumOfGuest(),
                booking.getBookingConfirmationCode(), roomResponse);
    }
}

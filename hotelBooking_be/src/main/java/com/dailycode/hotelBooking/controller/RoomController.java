package com.dailycode.hotelBooking.controller;

import com.dailycode.hotelBooking.exception.PhotoRetrievalException;
import com.dailycode.hotelBooking.exception.ResourceNotFoundException;
import com.dailycode.hotelBooking.model.BookedRoom;
import com.dailycode.hotelBooking.model.Room;
import com.dailycode.hotelBooking.response.RoomResponse;
import com.dailycode.hotelBooking.service.BookingService;
import com.dailycode.hotelBooking.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.rowset.serial.SerialBlob;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/rooms")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class RoomController {

    private final RoomService roomService;
    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<RoomResponse> addNewRoom(@RequestParam("photo") MultipartFile photo,
                                                   @RequestParam("roomType") String roomType,
                                                   @RequestParam("roomPrice") BigDecimal roomPrice) throws SQLException, IOException {
        Room savedRoom = roomService.addNewRoom(photo, roomType, roomPrice);
        RoomResponse response = new RoomResponse(savedRoom.getId(), savedRoom.getRoomType(), savedRoom.getRoomPrice());
        return ResponseEntity.ok(response);
    }

    @GetMapping("types")
    public List<String> getRoomTypes() {
        return roomService.getAllRoomTypes();
    }

    @GetMapping
    public ResponseEntity<List<RoomResponse>> getAllRooms() throws SQLException {
        List<Room> rooms = roomService.getAllRooms();
        List<RoomResponse> roomResponses = new ArrayList<>();
        for (Room room: rooms) {
            byte[] photoBytes = roomService.getRoomPhotoByRoomId(room.getId());
            if (photoBytes != null && photoBytes.length > 0) {
                String base64Photo = Base64.encodeBase64String(photoBytes);
                RoomResponse roomResponse = getRoomResponse(room);
                roomResponse.setPhoto(base64Photo);
                roomResponses.add(roomResponse);
            }
        }
        return ResponseEntity.ok(roomResponses);
    }

    @DeleteMapping("/delete/room/{roomId}")
    public ResponseEntity<Void> deleteRoom(@PathVariable("roomId") Long roomId) {
        roomService.deleteRoom(roomId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/update/{roomId}")
    public ResponseEntity<RoomResponse> updateRoom(@PathVariable Long roomId,
                                                   @RequestParam(required = false) String roomType,
                                                   @RequestParam(required = false) BigDecimal roomPrice,
                                                   @RequestParam(required = false) MultipartFile photo) throws IOException, SQLException {
        byte[] photoBytes = photo != null && !photo.isEmpty() ? photo.getBytes() : roomService.getRoomPhotoByRoomId(roomId);
        Blob photoBlob = photoBytes != null && photoBytes.length > 0 ? new SerialBlob(photoBytes) : null;
        Room room = roomService.updateRoom(roomId, roomType, roomPrice, photoBytes);
        room.setPhoto(photoBlob);
        RoomResponse roomResponse = getRoomResponse(room);
        return ResponseEntity.ok(roomResponse);
    }

    @GetMapping("/room/{roomId}")
    public ResponseEntity<Optional<RoomResponse>> getRoomById(@PathVariable Long roomId) {
        Optional<Room> room = roomService.getRoomById(roomId);
        return room.map(item -> {
            RoomResponse roomResponse = getRoomResponse(item);
            return ResponseEntity.ok(Optional.of(roomResponse));
        }).orElseThrow(() -> new ResourceNotFoundException("Room not found"));
    }

    private RoomResponse getRoomResponse(Room room) {
        List<BookedRoom> bookings = getAllBookingsByRoomId(room.getId());
        /*List<BookingResponse> bookingInfo = bookings
                .stream()
                .map(booking -> new BookingResponse(booking.getBookingId(), booking.getCheckInDate(), booking.getCheckOutDate(), booking.getBookingConfirmationCode()))
                .collect(Collectors.toList());*/
        byte[] photoBytes = null;
        Blob photoBlob = room.getPhoto();
        if (photoBlob != null) {
            try {
                photoBytes = photoBlob.getBytes(1, (int)photoBlob.length());
            } catch (SQLException e) {
                throw new PhotoRetrievalException("Error retrieving photo");
            }
        }
        return new RoomResponse(room.getId(), room.getRoomType(), room.getRoomPrice(), room.isBooked(), photoBytes);
    }

    private List<BookedRoom> getAllBookingsByRoomId(Long id) {
        return  bookingService.getAllBookingsByRoomId(id);
    }
}

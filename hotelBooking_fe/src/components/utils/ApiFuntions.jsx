import axios from 'axios'

export const api = axios.create({
    baseURL: "http://localhost:8080"
})

// add a new room
export async function addRoom(photo, roomType, roomPrice) {
    const formData = new FormData()
    formData.append("photo", photo)
    formData.append("roomType", roomType)
    formData.append("roomPrice", roomPrice)

    const response = await api.post("/api/v1/rooms", formData)
    if (response.status === 201) {
        return true
    }
    return false
}


// get all room types from the database
export async function getRoomTypes() {
    try {
        const response = await api.get("/api/v1/rooms/types")
        return response.data
    } catch(error) {
        throw new Error("Error fetching room types")
    }
}

// get all rooms from the database
export async function getAllRooms() {
    try {
        const result = await api.get("api/v1/rooms")
        return result.data
    } catch(error) {
        throw new Error("Error fetching rooms")
    }
}

// delete a room by the Id
export async function deleteRoom(roomId) {
    try {
        const result = await api.delete(`/api/v1/rooms/delete/room/${roomId}`)
        return result.data
    } catch(error) {
        throw new Error(`Error deleting room ${error.message}`)
    }
}

// update a room
export async function updateRoom(roomId, roomData) {
    const formData = new FormData()
    formData.append("roomType", roomData.roomType)
    formData.append("roomPrice", roomData.roomPrice)
    formData.append("photo", roomData.photo)
    const response = await api.put(`/api/v1/rooms/update/${roomId}`, formData)
    return response
}

// get a room by the id
export async function getRoomById(roomId) {
    try {
        const result = await api.get(`/api/v1/rooms/room/${roomId}`)
        return result.data
    } catch(error) {
        throw new Error(`Error fetching room ${error.message}`)
    }
}

// save a new booking to the database
export async function bookRoom(roomId, booking) {
    try {
        const response = await api.post(`/bookings/room/${roomId}/booking`, booking)
        return response.data
    } catch(error) {
        if (error.response && error.response.data) {
            throw new Error(error.response.data)
        } else {
            throw new Error(`Error booking room: ${error.message}`)
        }
    }
}

// gett all bookings from the database
export async function getAllBookings() {
    try {
        const result = await api.get("/bookings/all-bookings")
        return result.data
    } catch(error) {
        throw new Error(`Error fetching bookings : ${error.message}`)
    }
}


// get booking by the confirmation code
export async function getBookingByConfirmationCode(confirmationCode) {
    try {
        const result = await api.get(`/bookings/confirmation/${confirmationCode}`)
        return result.data
    } catch(error) {
        if (error.response && error.response.data) {
            throw new Error(error.response.data)
        } else {
            throw new Error(`Error find booking: ${error.message}`)
        }
    }
}

// cancel booking
export async function cancelBooking(bookingId) {
    try {
        const result = await api.delete(`/bookings/booking/${bookingId}/delete`)
        return result.data
    } catch(error) {
        throw new Error(`Error cancelling booking: ${error.message}`)
    }
}
import moment from "moment";
import React, { useEffect, useState } from "react";
import Button from "react-bootstrap/Button"
import { useNavigate } from "react-router-dom";

const BookingSummary = ({booking, payment, isFormValid, onConfirm}) => {

    const checkInDate = moment(booking.checkInDate)
    const checkOutDate = moment(booking.checkOutDate)
    const numOfDays = checkOutDate.diff(checkInDate, "days")
    const [isBookingConfirmed, setIsBookingConfirmed] = useState(false)
    const [isProcessingPayment, setIsProcessingPayment] = useState(false)

    const navigate = useNavigate()

    const handleConfirmBooking = () => {
        setIsProcessingPayment(true)
        setTimeout(() => {
            setIsProcessingPayment(false)
            setIsBookingConfirmed(true)
            onConfirm()
        }, 3000)
    }

    useEffect(() => {
        if (isBookingConfirmed) {
            navigate("/booking-success")
        }
    }, [isBookingConfirmed, navigate])

    return (
        <div className="card card-body mt-5">
            <h4>Reservation Summary</h4>
            <p>Full name: <strong>{booking.guestName}</strong></p>
            <p>Email: <strong>{booking.guestEmail}</strong></p>
            <p>Checkin date: <strong>{moment(booking.checkInDate).format("MMM Do YYYY")}</strong></p>
            <p>Checkout date: <strong>{moment(booking.checkOutDate).format("MMM Do YYYY")}</strong></p>
            <p>Number of days: <strong>{numOfDays}</strong></p>
            <div>
                <h5>Number of Guests</h5>
                <strong>Adult{booking.numberOfAdults > 1 ? "s" : ""}: {booking.numberOfAdults}</strong>
                <br/>
                <strong>Children: {booking.numberOfChildren}</strong>
            </div>
            {payment > 0 ? (
                <>
                    <p>Total Payment: <strong>${payment}</strong></p>
                    {isFormValid && !isBookingConfirmed ? (
                        <Button variant="success" onClick={handleConfirmBooking}>
                            {isProcessingPayment ? (
                                <>
                                    <span className="spinner-border spinner-border-sm mr-2" role="status" aria-hidden="true"></span>
                                    Booking Confirmed, redirecting to payment...
                                </>
                            ) : (
                                "Confirm Booking & proceed to payment"
                            )}
                        </Button>
                    ) : isBookingConfirmed ? (
                        <div className="d-flex justify-content-center align-items-center">
                            <div className="spinner-border text-primary" role="status">
                                <span className="sr-only">Loading ...</span>
                            </div>
                        </div>
                    ) : null}
                </>
            ) : (
                <p className="text-danger">
                    Checkout date must be after checkin date
                </p>
            )}
        </div>
    )
}

export default BookingSummary
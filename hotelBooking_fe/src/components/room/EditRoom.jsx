import React, { useEffect, useState } from "react";
import { getRoomById, updateRoom } from "../utils/ApiFuntions";
import { Link, useParams } from 'react-router-dom';
import RoomTypeSelector from "../common/RoomTypeSelector";

const EditRoom = () => {

    const [room, setRoom] = useState({
        photo: null,
        roomType: "",
        roomPrice: ""
    })

    const [imagePreview, setImagePreview] = useState("")
    const [successMessage, setSuccessMessage] = useState("")
    const [errorMessage, setErrorMessage] = useState("")

    const {roomId} = useParams()

    const handleImageChange = (e) => {
        const selectedImage = e.target.files[0]
        setRoom({...room, photo: selectedImage})
        setImagePreview(URL.createObjectURL(selectedImage))
    }

    const handleRoomInputChange = (event) => {
        const {name, value} = event.target
        setRoom({...room, [name]: value})
    }

    useEffect(() => {
        const fetchRoom = async () => {
            try {
                const roomData = await getRoomById(roomId)
                setRoom(roomData)
                setImagePreview(roomData.photo)
            } catch (error) {
                console.log(error)
            }
        }
        fetchRoom()
    }, [roomId])

    const handleSubmit = async(e) => {
        e.preventDefault()
        try {
            const response = await updateRoom(roomId, room)
            if (response.status === 200) {
                setSuccessMessage("Room updated successfully")
                const updatedRoomData = await getRoomById(roomId)
                setRoom(updatedRoomData)
                setImagePreview(updatedRoomData.photo)
                setErrorMessage("")
            } else {
                setErrorMessage("Error updating room")
            }
        } catch (error) {
            setErrorMessage(error.message)
        }
        setTimeout(() => {
            setSuccessMessage("")
            setErrorMessage("")
        }, 3000)
    }

    return (
        <section className='container mt-5 mb-5'>
            <div className='room justify-content-center'>
                <div className='col-md-8 col-lg-6'>
                    <h2 className='mt-5 mb-2'>Edit room</h2>

                    {successMessage && (
                        <div className='alert alert-success fade show'>{successMessage}</div>
                    )}

                    {errorMessage && (
                        <div className='alert alert-danger fade show'>{errorMessage}</div>
                    )}

                    <form onSubmit={handleSubmit}>
                        <div className='mb-3'>
                            <label htmlFor='roomType' className='form-label'>Room type</label>
                            <div>
                                <RoomTypeSelector 
                                    handleRoomInputChange={handleRoomInputChange} 
                                    newRoom={room}/>
                            </div>
                        </div>
                        <div className='mb-3'>
                            <label htmlFor='roomPrice' className='form-label'>Room price</label>
                            <input className='form-control' 
                                required
                                id='roomPrice'
                                name='roomPrice'
                                type='number'
                                value={room.roomPrice}
                                onChange={handleRoomInputChange}/>
                        </div>
                        <div className='mb-3'>
                            <label htmlFor='photo' className='form-label'>Photo</label>
                            <input className='form-control' 
                                required
                                id='photo'
                                name='photo'
                                type='file'
                                // value={newRoom.photo}
                                onChange={handleImageChange}/>
                            {imagePreview && (
                                <img src={imagePreview} style={{maxWidth: "400px", maxHeight:"400px"}} className='mb-3' alt='Preview room photo'/>
                            )}
                        </div>
                        <div className='d-grid d-md-flex mt-2'>
                            <Link to={"/existing-rooms"} className="btn btn-outline-info ml-5">Back</Link>
                            <button type="submit" className='btn btn-outline-warning ml-5'>Edit Room</button>
                        </div>
                    </form>
                </div>
            </div>
        </section>
    )
}

export default EditRoom
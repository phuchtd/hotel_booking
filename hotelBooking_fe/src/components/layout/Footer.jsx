import React from "react";
import { Container, Row, Col } from "react-bootstrap";

const Footer = () => {

    let today = new Date();

    return (
       <footer>
            <div className="by-dark text-light py-3 footer mt-lg-5">
                <Row>
                    <Col xs={12} md={12} className="text-center">
                        <p>&copy; {today.getFullYear()} lakeSide Hotel</p>
                    </Col>
                </Row>
            </div>
       </footer>
    )
}

export default Footer
import React, { Component } from 'react';
import { Loading } from './LoadingComponent';
import {
    Col, Button, Row, Label, NavLink,
} from 'reactstrap';

import { Link ,Redirect } from 'react-router-dom';
import axios from "axios";
import {FormControl, InputGroup} from "react-bootstrap";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faSearch, faTimes} from "@fortawesome/free-solid-svg-icons";

const required = val => val && val.length;

class Welcome extends Component{
    constructor(props) {
        super(props);
        this.state = {
            books : [],
            search : '',
            currentPage : 1,

            sortToggle : true
        };
        this.searchforData = this.searchforData.bind(this);
    }
    searchforData = (currentPage) => {
        currentPage -= 1;
        axios.get("http://localhost:8081/rest/books/search/"+this.state.search+"?page="+1+"&size="+this.state.booksPerPage)
            .then(response => response.data)
            .then((data) => {
                this.setState({
                    books: data.content,
                    totalPages: data.totalPages,
                    totalElements: data.totalElements,
                    currentPage: data.number + 1
                });
            });
    };


    render(){
        return(
            <div className="container ">
                <Row>
                    <Col>
                        <div>
                            <img alt="Google" height="300" id="hplogo"
                                 src="https://9to5google.com/wp-content/uploads/sites/4/2020/04/popular-google-doodle-games.jpg?quality=82&strip=all"
                                 width="500">
                            </img>
                        </div>
                    </Col>
                </Row>
                <div className="row ">
                    <div>
                            <Row className="form-group">
                                <Col xs={12} md={{ size: 6, offset: 3 }}>
                                    <InputGroup size="sm">
                                        <FormControl placeholder="Search" name="search"
                                                     className={"info-border bg-dark text-white"}
                                                     onChange={this.searchChange}/>
                                        <InputGroup.Append>
                                            <Button size="sm" variant="outline-info" type="button" onClick={this.searchforData(1)}>
                                                <FontAwesomeIcon icon={faSearch}/>
                                            </Button>
                                            <Button size="sm" variant="outline-danger" type="button" onClick={this.cancelSearch}>
                                                <FontAwesomeIcon icon={faTimes} />
                                            </Button>
                                        </InputGroup.Append>
                                    </InputGroup>
                                </Col>
                            </Row>

                    </div>
                </div>
            </div>
        )
    }
}

export default Welcome;
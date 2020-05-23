import React, {Component} from 'react';
import './Style.css';
import {Card, Table,Button, InputGroup, FormControl} from 'react-bootstrap';
import {FontAwesomeIcon} from '@fortawesome/react-fontawesome';
import {
    faStepBackward,
    faFastBackward,
    faStepForward,
    faFastForward,
    faSearch,
    faTimes,
    faMicrophone, faAssistiveListeningSystems
} from '@fortawesome/free-solid-svg-icons';
import axios from 'axios';

const SpeechRecognition = window.webkitSpeechRecognition
var recognition = new SpeechRecognition();
recognition.continous = true
recognition.interimResults = true
recognition.lang = 'en-US'

 class BookList extends Component {
    constructor(props) {
        super(props);
        this.state = {
            books : [],
            countryName: '',
            countryCode: '',
            search : '',
            interimTranscript:'',
            finalTranscript:'',
            listening: false,
            currentPage : 1,
            booksPerPage : 10,
        };
        this.toggleListen = this.toggleListen.bind(this)
        this.handleListen = this.handleListen.bind(this)
    }
     toggleListen() {
         this.setState({
             listening: !this.state.listening
         }, this.handleListen)
     }
     handleListen(){

         if (this.state.listening) recognition.start()

         let finalTranscript = ''
         recognition.onresult = event => {
             let interimTranscript = ''

             for (let i = event.resultIndex; i < event.results.length; i++) {
                 const transcript = event.results[i][0].transcript;
                 if (event.results[i].isFinal) finalTranscript += transcript + ' ';
                 else interimTranscript += transcript;
             }
            this.setState({interimTranscript: interimTranscript,finalTranscript:finalTranscript})
         }

    }

    getGeoInfo = () => {
        axios.get('https://ipapi.co/json/').then((response) => {
            let data = response.data;
            this.setState({
                countryName: data.country_name,
                countryCode: data.country_calling_code
            });
        }).catch((error) => {
            console.log(error);
        });
    };


    findAllBooks(currentPage) {
        currentPage -= 1;
        axios.get("http://localhost:8081/rest/api?pageNumber="+currentPage+"&pageSize="+this.state.booksPerPage)
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



    changePage = event => {
        let targetPage = parseInt(event.target.value);
        if(this.state.search) {
            this.searchData(targetPage);
        } else {
            this.findAllBooks(targetPage);
        }
        this.setState({
            [event.target.name]: targetPage
        });
    };

    firstPage = () => {
        let firstPage = 1;
        if(this.state.currentPage > firstPage) {
            if(this.state.search) {
                this.searchData(firstPage);
            } else {
                this.findAllBooks(firstPage);
            }
        }
    };

    prevPage = () => {
        let prevPage = 1;
        if(this.state.currentPage > prevPage) {
            if(this.state.search) {
                this.searchData(this.state.currentPage - prevPage);
            } else {
                this.findAllBooks(this.state.currentPage - prevPage);
            }
        }
    };

    lastPage = () => {
        let condition = Math.ceil(this.state.totalElements / this.state.booksPerPage);
        if(this.state.currentPage < condition) {
            if(this.state.search) {
                this.searchData(condition);
            } else {
                this.findAllBooks(condition);
            }
        }
    };

    nextPage = () => {
        if(this.state.currentPage < Math.ceil(this.state.totalElements / this.state.booksPerPage)) {
            if(this.state.search) {
                this.searchData(this.state.currentPage + 1);
            } else {
                this.findAllBooks(this.state.currentPage + 1);
            }
        }
    };

    searchChange = event => {
        this.setState({
            [event.target.name] : event.target.value
        });
    };

    cancelSearch = () => {
        this.setState({"search" : ''});
         this.findAllBooks(this.state.currentPage);
    };

    searchData = (currentPage) => {
        currentPage -= 1;
        axios.get("http://localhost:8081/rest/api/search/"+this.state.search+"?page="+currentPage+"&size="+this.state.booksPerPage)
            .then(response => response.data)
            .then((data) => {
                this.setState({
                    books: data.content,
                    totalPages: data.totalPages,
                    totalElements: data.totalElements,
                    currentPage: data.number + 1
                });
            });
        axios.get("http://localhost:8081/rest/api/location/"+this.state.countryName)
            .then(response=> response.data);
    };
     searchVoiceData = (currentPage) => {
         currentPage -= 1;
         axios.get("http://localhost:8081/rest/api/search/"+this.state.finalTranscript  +"?page="+currentPage+"&size="+this.state.booksPerPage)
             .then(response => response.data)
             .then((data) => {
                 this.setState({
                     books: data.content,
                     totalPages: data.totalPages,
                     totalElements: data.totalElements,
                     currentPage: data.number + 1
                 });
             });
         axios.get("http://localhost:8081/rest/api/location/"+this.state.countryName)
             .then(response=> response.data);
     };
    componentDidMount() {
        if ("geolocation" in navigator) {
            console.log("Available");
        } else {
            console.log("Not Available");
        }
        this.getGeoInfo()
    }

    render() {
        const {books, currentPage, totalPages, search} = this.state;
        return <div>
            <div style={{"display":this.state.show ? "block" : "none"}}>
            </div>
            <Card className={"border border-dark bg-dark text-white"}>
                <Card.Header>
                    <div style={{"float":"left"}}>
                         Google
                    </div>
                    <div style={{"float":"right"}}>
                         <InputGroup size="sm">
                            <FormControl placeholder="Enter Search Query" name="search" value={search}
                                className={"info-border bg-dark text-white"}
                                onChange={this.searchChange}/>
                            <InputGroup.Append>
                                <Button size="sm" variant="outline-info" type="button" onClick={this.searchData}>
                                    <FontAwesomeIcon icon={faSearch}/>
                                </Button>

                                <Button size="sm"  type="button"  onClick={this.toggleListen}>
                                    <FontAwesomeIcon icon={faMicrophone} />
                                </Button>
                                <Button size="sm" variant="outline-danger" type="button" onClick={this.cancelSearch}>
                                    <FontAwesomeIcon icon={faTimes} />
                                </Button>
                           </InputGroup.Append>
                         </InputGroup>
                    </div>
                </Card.Header>
                <div className={container}>
                    <p>  Your Voice search is: {this.state.finalTranscript}</p>
                        <p>Press the following button to search  <Button size="sm" variant="outline-info" type="button" onClick={this.searchVoiceData}>
                            <FontAwesomeIcon icon={faAssistiveListeningSystems}/>
                        </Button></p>
                </div>

                <Card.Body>
                    <Table bordered hover striped variant="dark">
                        <thead>
                            <tr>
                              <th>Search Results</th>
                            </tr>
                          </thead>
                          <tbody>
                            {
                                books.length === 0 ?
                                <tr align="center">
                                  <td colSpan="7">No Results yet.</td>
                                </tr> :
                                books.map((book) => <tr key={book.id}>
                                    <td>
                                   <a href={book.urls}>{book.urls}</a>
                                    </td>
                                </tr>)
                            }
                          </tbody>
                    </Table>
                    <p>Country Name: {this.state.countryName}</p>
                </Card.Body>
                {books.length > 0 ?
                    <Card.Footer>
                        <div style={{"float":"left"}}>
                            Showing Page {currentPage} of {totalPages}
                        </div>
                        <div style={{"float":"right"}}>
                            <InputGroup size="sm">
                                <InputGroup.Prepend>
                                    <Button type="button" variant="outline-info" disabled={currentPage === 1 ? true : false}
                                        onClick={this.firstPage}>
                                        <FontAwesomeIcon icon={faFastBackward} /> First
                                    </Button>
                                    <Button type="button" variant="outline-info" disabled={currentPage === 1 ? true : false}
                                        onClick={this.prevPage}>
                                        <FontAwesomeIcon icon={faStepBackward} /> Prev
                                    </Button>
                                </InputGroup.Prepend>
                                <FormControl className={"page-num bg-dark"} name="currentPage" value={currentPage}
                                    onChange={this.changePage}/>
                                <InputGroup.Append>
                                    <Button type="button" variant="outline-info" disabled={currentPage === totalPages ? true : false}
                                        onClick={this.nextPage}>
                                        <FontAwesomeIcon icon={faStepForward} /> Next
                                    </Button>
                                    <Button type="button" variant="outline-info" disabled={currentPage === totalPages ? true : false}
                                        onClick={this.lastPage}>
                                        <FontAwesomeIcon icon={faFastForward} /> Last
                                    </Button>
                                </InputGroup.Append>
                            </InputGroup>
                        </div>

                    </Card.Footer> : null
                 }
            </Card>
        </div>;
    }
}
const styles = {
    container: {
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center',
        textAlign: 'center',
        marginLeft:'10px'
    },
    button: {
        width: '60px',
        height: '60px',
        background: 'lightblue',
        borderRadius: '50%',
        margin: '6em 0 2em 0'
    },
    interim: {
        color: 'gray',
        border: '#ccc 1px solid',
        padding: '1em',
        margin: '1em',
        width: '300px'
    },
    final: {
        color: 'black',
        border: '#ccc 1px solid',
        padding: '1em',
        margin: '1em',
        width: '300px'
    }
}

const { container, button } = styles

export default BookList;
import React, {Component} from 'react';
import './Style.css';
import './AutoComplete.css';
import LoadingComponent from './LoadingComponent';
import {Card, Table, Button, InputGroup, FormControl} from 'react-bootstrap';
import {FontAwesomeIcon} from '@fortawesome/react-fontawesome';
import {
    faStepBackward,
    faFastBackward,
    faStepForward,
    faFastForward,
    faSearch,
    faTimes,
    faMicrophone, faAssistiveListeningSystems, faImage, faTaxi, faToggleOn, faToggleOff
} from '@fortawesome/free-solid-svg-icons';
import axios from 'axios';

const SpeechRecognition = window.webkitSpeechRecognition
var recognition = new SpeechRecognition();
recognition.continous = true
recognition.interimResults = true
recognition.lang = 'en-US'

 class SearchEngine extends Component {
    constructor(props) {
        super(props);
        this.state = {
            SearchResults : [],
            isToggled:0,
            image:0,
            ReservedQueries: {},
            countryName: '',
            countryCode: '',
            search : '',
            suggestions:[]    ,
            interimTranscript:'',
            finalTranscript:'',
            isLoading:false,
            text:'',
            listening: false,
            id:Math.floor((Math.random() * 10000) + 1),
            currentPage : 1,
            SearchResultsPerPage : 10,
        };
        this.toggleListen = this.toggleListen.bind(this)
        this.handleListen = this.handleListen.bind(this)
        this.erase = this.erase.bind(this)


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
            });
        }).catch((error) => {
            console.log(error);
        });
    };

    changePage = event => {
        let targetPage = parseInt(event.target.value);
       if(this.state.image===0){ this.searchData(targetPage);}
           else {this.searchImage(targetPage);}

        this.setState({
            [event.target.name]: targetPage
        });
    };

    firstPage = () => {
        let firstPage = 1;
        if(this.state.currentPage > firstPage) {
            if(this.state.image===0){this.searchData(firstPage);}
            else {this.searchImage(firstPage);}


        }
    };

    prevPage = () => {
        let prevPage = 1;
        if(this.state.currentPage > prevPage) {
         if(this.state.image===0){this.searchData(this.state.currentPage - prevPage);
         }
         else{this.searchImage(this.state.currentPage - prevPage);}
        }
    };

    lastPage = () => {
        let condition = Math.ceil(this.state.totalElements / this.state.SearchResultsPerPage);
        if(this.state.currentPage < condition) {
   if(this.state.image===0){this.searchData(condition);}
   else {this.searchImage(condition);}


        }
    };

    nextPage = () => {
        if(this.state.currentPage < Math.ceil(this.state.totalElements / this.state.SearchResultsPerPage)) {

                if(this.state.image===0){
                    this.searchData(this.state.currentPage + 1);
                }
                else if(this.state.image===1) {
                    this.searchImage(this.state.currentPage + 1);
                }

        }
    };
    cancelSearch = () => {

        this.setState({"search" : ''});
        this.getResponse()

    };
     searchImage = (currentPage) => {

         this.setState({isLoading:true,searchTemp:this.state.search})
         currentPage -= 1;
         axios.get("http://localhost:8081/rest/api/search/"+this.state.countryName+"/"+this.state.id+"/"+this.state.search+"/"+this.state.isToggled+"/1?page="+currentPage+"&size="+this.state.SearchResultsPerPage)
             .then(response =>{
                 this.setState({
                     SearchResults: response.data.content,
                     totalPages: response.data.totalPages,
                     totalElements: response.data.totalElements,
                     currentPage: response.data.number + 1,
                     isLoading:false
                 }); if(this.state.isToggled===1){this.setState({isToggled:0})};}
             )
         let x=(this.state.search==="")
         if(!x){
             this.getResponse()
             if(this.state.ReservedQueries.find(element=> element===this.state.search)===undefined) {
                 const posted = {
                     "name": this.state.search,
                     "id": Math.floor((Math.random() * 1000) + 1)
                 }
                 axios.post("http://localhost:3000/users", posted).then(r => r.data)
             }}

         this.setState({suggestions:[],image:1})
     };
    searchData = (currentPage) => {

        this.setState({isLoading:true,searchTemp:this.state.search})
        currentPage -= 1;
        axios.get("http://localhost:8081/rest/api/search/"+this.state.countryName+"/"+this.state.id+"/"+this.state.search+"/"+this.state.isToggled+"/0?page="+currentPage+"&size="+this.state.SearchResultsPerPage)
       .then(response =>{
        this.setState({
            SearchResults: response.data.content,
            isLoading:false,
            totalPages: response.data.totalPages,
            totalElements: response.data.totalElements,
            currentPage: response.data.number + 1
        });
           if(this.state.isToggled===1){this.setState({isToggled:0})};}
       )
        let x=(this.state.search==="")
        if(!x){
            this.getResponse()
            if(this.state.ReservedQueries.find(element=> element===this.state.search)===undefined) {
                const posted = {
                    "name": this.state.search,
                    "id": Math.floor((Math.random() * 1000) + 1)
                }
                axios.post("http://localhost:3000/users", posted).then(r => r.data)
            }}

this.setState({suggestions:[],image:0})
    };

    searchVoiceData = (currentPage) => {

        this.setState({isLoading:true})
         currentPage -= 1;
         axios.get("http://localhost:8081/rest/api/search/"+this.state.countryName+"/"+this.state.id+"/"+this.state.finalTranscript+"/"+this.state.isToggled+"/0?page="+currentPage+"&size="+this.state.SearchResultsPerPage)
             .then(response => response.data)
             .then((data) => {
                 this.setState({
                     SearchResults: data.content,
                     isLoading:false,
                     totalPages: data.totalPages,
                     totalElements: data.totalElements,
                     currentPage: data.number + 1
                 });
                 if(this.state.isToggled===1){this.setState({isToggled:0})};
             });
        this.getResponse()
        let x=(this.state.finalTranscript==="")
        if(x===false){
         if(this.state.ReservedQueries.find(element=> element===this.state.finalTranscript)===undefined) {
             const posted = {
                "name": this.state.finalTranscript,
                "id": Math.floor((Math.random() * 1000) + 1)
            }
            axios.post("http://localhost:3000/users", posted).then(r => r.data)
        }}


    };
    componentDidMount() {
        this.getGeoInfo()
        this.getResponse()
    }
    getResponse(){
        let ReservedQ=[]
        axios.get("http://localhost:3000/users")
            .then((response) => { ReservedQ = response.data.map(x => x.name)
                this.setState({ReservedQueries: ReservedQ})})
    }
    onTextChange=(e)=>{
        this.getResponse()
        const value = e.target.value;
        let suggestions=[];
        if(value.length > 0){
            const regex=new RegExp(`^${value}`,'i')
            suggestions=this.state.ReservedQueries.sort().filter(v=>regex.test(v));
        }
        this.setState({suggestions:suggestions,search: value})
    }
    suggestionselected(value){
        this.setState(()=>({
        search:value,
        suggestions:[]
        }))}
    renderSuggestion(){
         const {suggestions}=this.state;
         if(suggestions.length===0){return null}
         return(<div className="AutoComplete">
             <ul>
                 {suggestions
                     .map(item=><li className="AutoComplete ul"
                                    onClick={()=>this.suggestionselected(item)}>{item}</li>)}</ul></div>)
     }
     erase(){
        if(this.state.isToggled===1){
            this.setState({isToggled:0})
        }
    else{
            this.setState({isToggled:1})
        }
    }

render() {
        const {SearchResults, currentPage, totalPages, search} = this.state;
        return <div className="WrappingElement">
            <Card className={"border border-dark  text-black"}>
                <div>
                    <img alt="Google" height="140" id="hplogo"
                         src="https://www.3m.com/wps/wcm/connect/fd542ff6-2e40-4c53-b152-27a818bcdf23/3M-Scotchlite-Black-Reflective-stretch-icon.jpg?MOD=AJPERES&CACHEID=ROOTWORKSPACE-fd542ff6-2e40-4c53-b152-27a818bcdf23-lL4hDG0"
                         width="300">
                    </img>
                </div>
                <Card.Header>

                    <div className="AutoComplete">
                        <InputGroup size="sm">

                            <input value={search} onChange={this.onTextChange } type="text" />

                        <InputGroup.Append>
                                <Button size="sm" variant="outline-info" type="button" onClick={this.searchData}>
                                    <FontAwesomeIcon icon={faSearch}/>
                                </Button>
                            <Button size="sm" variant="outline-primary" type="button"  onClick={this.searchImage}>
                                <FontAwesomeIcon icon={faImage} />
                            </Button>
                                <Button size="sm"  type="button"  onClick={this.toggleListen}>
                                    <FontAwesomeIcon icon={faMicrophone} />
                                </Button>
                                <Button size="sm" variant="outline-danger" type="button" onClick={this.cancelSearch}>
                                    <FontAwesomeIcon icon={faTimes} />
                                </Button>
                            {this.state.isToggled===0?(
                                <Button size="sm" variant="outline-danger" type="button" onClick={this.erase}>
                                    <FontAwesomeIcon icon={faToggleOff} />
                                </Button>):(<Button size="sm" variant="outline-danger" type="button" onClick={this.erase}>
                                <FontAwesomeIcon icon={faToggleOn} />
                            </Button>)}

                            </InputGroup.Append>
                        </InputGroup>
                       {this.renderSuggestion()}
                    </div>

                </Card.Header>
                <div className={container}>
                    <p>  Your Voice search is: {this.state.finalTranscript}</p>
                        <p>Press the following button to search  <Button size="sm" variant="outline-info" type="button" onClick={this.searchVoiceData}>
                            <FontAwesomeIcon icon={faAssistiveListeningSystems}/>
                        </Button></p>
                </div>
                {this.state.image===0 ? (
                    <div>
                    <Card.Body>
                        <Table bordered hover striped variant="dark">
                            <thead>
                            <tr>
                                <th>Search Results</th>
                            </tr>
                            </thead>
                            <tbody>
                            {
                                this.state.isLoading ?
                                    <tr align="center">
                                       <LoadingComponent/>
                                    </tr> :
                                    SearchResults.map((SearchResult) =>
                                        <div className="rc" >
                                            <div className="r">
                                                <h3>{SearchResult.title}</h3>
                                            </div>
                                        <h4><a href={SearchResult.urls}>{SearchResult.urls}</a></h4>
                                        <span>
                                        {SearchResult.description}
                                        </span>
                                            <hr/>
                                        </div>)
                            }
                            </tbody>
                        </Table>
                    </Card.Body>
                    {SearchResults.length > 0 ?
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

                            </Card.Footer> : null} </div>):(
                    <div>
                        <Card.Body>
                            <Table bordered hover striped variant="dark">
                                <thead>
                                <tr>
                                    <th>Search Results</th>
                                </tr>
                                </thead>
                                <tbody className="FixingTheImage">
                                {
                                    this.state.isLoading ?
                                        <LoadingComponent/>:
                                        SearchResults.map((SearchResult) => <div>
                                            <img className="FixingTheImageItself" src={SearchResult.urls}></img>
                                        </div>)
                                }
                                </tbody>
                            </Table>
                        </Card.Body>
                        {SearchResults.length > 0 ?
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

                            </Card.Footer> : null} </div>
                )}
            </Card>

            <Button href="/Trending">
                <FontAwesomeIcon icon={faTaxi} /> Reveal Trends
            </Button>

        </div>

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

const { container } = styles

export default SearchEngine;
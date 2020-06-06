import React, {Component} from 'react';
import axios from 'axios';
import Histogram from 'react-chart-histogram';
import isLoading from './LoadingComponent'
import Select from 'react-select'
import countryList from 'react-select-country-list'
import {Button, Col} from "react-bootstrap";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faTaxi} from "@fortawesome/free-solid-svg-icons";
import Row from "react-bootstrap/Row";
import Loading from "./LoadingComponent";

class Trending extends Component {
    constructor(props) {
        super(props);
        this.options = countryList().getData()

        this.state = {
            isLoading:false,
            options: this.options,
            value: null,
            data:[],
            countOccurrences:[],
            countryName:'',
            labels:[],
            Location:'',
            id:Math.floor((Math.random() * 10000) + 1),
        };


    }
    // componentDidMount() {
    //     this.getGeoInfo()
    // }
    getGeoInfo = () => {
        this.setState({isLoading:true})
        let ReservedLabels=[]
        let ReservedY=[]
            console.log(this.state.value.label)
            axios.get("http://localhost:8081/rest/Trending/trend/"+this.state.id+"/"+this.state.value.label)
                .then((response) =>
                { ReservedLabels = response.data.content.map(x => x.name);
                    this.setState({labels: ReservedLabels})
                    ReservedY=response.data.content.map(x => x.count);
                    this.setState({countOccurrences: ReservedY,isLoading:false}

                    )})
  }
    changeHandler = value => {
        this.setState({ value })
    }
    render(){

        const options = { fillColor: '#7f6464', strokeColor: '#4a4aaa' };
        return (
            <div className="WrappingElement">
                <Row><Col md={9}><Select
                    options={this.state.options}
                    value={this.state.value}
                    onChange={this.changeHandler}
                />  </Col><Col md={3}><Button onClick={this.getGeoInfo}>
                    <FontAwesomeIcon icon={faTaxi} /> Reveal Trends
                </Button>
                </Col></Row>
                {this.state.isLoading ?
                <Loading/>:

                    <Histogram
                        xLabels={this.state.labels}
                        yValues={this.state.countOccurrences}
                        width='1000'
                        height='1000'
                        options={options}
                    />}

            </div>
        )
    }
}
export default Trending;
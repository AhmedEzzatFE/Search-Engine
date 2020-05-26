import React, {Component} from 'react';
import axios from 'axios';
import Histogram from 'react-chart-histogram';
import Spinner from "react-bootstrap/Spinner";


class Trending extends Component {
    constructor(props) {
        super(props);
        this.state = {
            data:[],
            countOccurrences:[],
            countryName:'',
            labels:[],
            Location:'',
            id:Math.floor((Math.random() * 10000) + 1),
        };


    }
    componentDidMount() {
        this.getGeoInfo()
    }
    getGeoInfo = () => {
        let ReservedLabels=[]
        let ReservedY=[]

        axios.get('https://ipapi.co/json/').then((response) => {
            let data = response.data;
            this.setState({
                countryName: data.country_name,
            });
            axios.get("http://localhost:8081/rest/Trending/trend/"+this.state.id+"/"+this.state.countryName)
                .then((response) =>
                { ReservedLabels = response.data.content.map(x => x.name);
                    this.setState({labels: ReservedLabels})
                    ReservedY=response.data.content.map(x => x.count);
                    this.setState({countOccurrences: ReservedY}

                    )})})
  }
    render(){

        const options = { fillColor: '#7f6464', strokeColor: '#4a4aaa' };
        return (
            <div>

                {this.state.labels.length===0?
                <div> Getting Trends
                </div>: <Histogram
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
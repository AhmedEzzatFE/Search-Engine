import React, {Component} from 'react';

import axios from 'axios';
import Histogram from 'react-chart-histogram';


class Trending extends Component {
    constructor(props) {
        super(props);
        this.state = {
            id:Math.floor((Math.random() * 10000) + 1),
        };


    }
    componentDidMount() {
        axios.get("http://localhost:8081/rest/Trending/trend/"+this.state.id)
            .then(response =>{
                console.log(response.data);
                this.setState({
                    books: response.data.content,
                    totalPages: response.data.totalPages,
                    totalElements: response.data.totalElements,
                    currentPage: response.data.number + 1
                });}
            )
    }
    render() {
        const labels = ['2016', '2017', '2018'];
        const data = [324, 45, 672];
        const options = { fillColor: '#FFFFFF', strokeColor: '#0000FF' };
        return (
            <div>
                <Histogram
                    xLabels={labels}
                    yValues={data}
                    width='1000'
                    height='1000'
                    options={options}
                />
            </div>
        )
    }
}
export default Trending;
import React from 'react';
import './App.css';
import {Container, Row, Col} from 'react-bootstrap';
import {BrowserRouter as Router, Switch, Route} from 'react-router-dom';
import SearchEngine from './components/SearchEngine';
import Trending from './components/Trending';

export default function App() {
  return (
    <Router>
        <Container>
                <div className={"margin-top"}>
                    <Switch>
                        <Route path="/" exact component={SearchEngine}/>
                        <Route path="/Trending" exact component={Trending}/>

                    </Switch>
                </div>

        </Container>
    </Router>
  );
}

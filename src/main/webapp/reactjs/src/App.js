import React from 'react';
import './App.css';
import {Container, Row, Col} from 'react-bootstrap';
import {BrowserRouter as Router, Switch, Route} from 'react-router-dom';
import BookList from './components/BookList';
export default function App() {
  return (
    <Router>
        <Container>
            <Row>
                <Col lg={12} className={"margin-top"}>
                    <Switch>
                        <Route path="/" exact component={BookList}/>
                    </Switch>
                </Col>
            </Row>
        </Container>
    </Router>
  );
}

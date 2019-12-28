import React, {Component} from 'react';
import Container from 'react-bootstrap/lib/'
import Row from 'react-bootstrap/lib/Row'
import Col from 'react-bootstrap/lib/Col'


class WorkSpace extends Component{

render(){
  return(
  <Container>
  
  <Row>
    <Col xs={6} md={4}>
      xs=12 md=8
    </Col>
    <Col xs={6} md={8}>
      xs=6 md=4
    </Col>
  </Row>
  </Container>);
}



}


export default WorkSpace;

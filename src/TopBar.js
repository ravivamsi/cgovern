import React, {Component} from 'react';
import Navbar from 'react-bootstrap/lib/Navbar';
import FormControl from 'react-bootstrap/lib/FormControl';
import Button from 'react-bootstrap/lib/Button';
import Nav from 'react-bootstrap/lib/Nav';
import Form from 'react-bootstrap/lib/Form';



class TopBar extends Component{


  render(){
    return(
    
    
    //   <Navbar bg="dark" variant="dark">
    //   <Navbar.Brand href="#home">CGovern</Navbar.Brand>
    //   <Nav className="mr-auto">
    //     <Nav.Link href="#home">Home</Nav.Link>
    //     <Nav.Link href="#projects">Projects</Nav.Link>
    //     <Nav.Link href="#pricing">Pipelines</Nav.Link>
    //     <Nav.Link href="#documentation">Documentation</Nav.Link>
    //   </Nav>
    //   <Form inline>
    //     <FormControl type="text" placeholder="Search" className="mr-sm-2" />
    //     <Button variant="outline-info">Search</Button>
    //   </Form>
    // </Navbar>
    

    <div className="header" >
          <h3 className="text-muted" align="left">CGovern</h3>
      </div>
    
    );
  }

}


export default TopBar;

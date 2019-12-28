import React, {Component} from 'react';
import Breadcrumb from 'react-bootstrap/lib/Breadcrumb'




class BreadCrumb extends Component{

  render(){
    return(

      <Breadcrumb align="left">
  <Breadcrumb.Item href="#">Home</Breadcrumb.Item>
  <Breadcrumb.Item href="#/projects">
    Projects
  </Breadcrumb.Item>
  <Breadcrumb.Item active href="#/projects/plans">Plans</Breadcrumb.Item>
</Breadcrumb>

    );
  }

}


export default BreadCrumb;

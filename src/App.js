import React, {Component} from 'react';
import './App.css';
import axios from 'axios';
import Footer from './Footer';
import TopBar from './TopBar';
import BreadCrumb from './BreadCrumb';
import Router from './Router';
import TopBarSample from './TopBarSample';
import WorkSpace from './WorkSpace';




  
class App extends Component{

  state={
    isLoading: true,
    projects:[]
  }



  async componentDidMount(){
    const response = await axios.get("http://jsonplaceholder.typicode.com/users",
        { headers: {'Content-Type': 'application/json'}}
      );
    const body = await response.data;
    this.setState({
      projects: body,
      isLoading: false
    });
  }


    render(){
      const {projects, isLoading} = this.state;

      if(isLoading){
        return <p>Loading .... </p>;
      }

    return (


      <div className="App">

      <TopBar/>
      <BreadCrumb/>
      {/* <Router/> */}

      {/* <WorkSpace/> */}
{/*       
        <header className="App-header">

          <div className="App-intro">
          <h2>Projects</h2>


          {projects.map(project=>
              <div key={project.id}>
              {project.name}
              </div>
          )}
          </div>
        </header>

 */}
        <Footer/>
      </div>

      // <Footer/>




    );
  }


}

export default App;

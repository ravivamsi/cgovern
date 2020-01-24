import React, {Component} from 'react';
import './App.css';
import axios from 'axios';
import Footer from './Footer';
import TopBar from './TopBar';
import BreadCrumb from './BreadCrumb';


  
class App extends Component{

  state={
    isLoading: true,
    projects:[]
  }



  async componentDidMount(){


    const resp = await axios.get("http://localhost:8080/v2/health",{ headers: {'Content-Type': 'application/json','Origin':'*'}});
    const response = await axios.get("http://jsonplaceholder.typicode.com/users",
        { headers: {'Content-Type': 'application/json'}}
      );

    const vitalbody = await resp.data;
    const body = await response.data;

    console.log(vitalbody.appname)

    this.setState({
      projects: body,
      health: vitalbody.appname,
      isLoading: false
    });
  }



    render(){
      const {projects, isLoading,health} = this.state;

      if(isLoading){
        return <p>Loading .... </p>;
      }

    return (


      <div className="App">

      <TopBar/>

      
      <BreadCrumb/>
      {/* <Router/> */}

      {/* <WorkSpace/> */}
      
        <header className="App-header">

          <div className="App-intro">
          <h2>Projects</h2>
          <h2> {health} </h2>

          {projects.map(project=>
              <div key={project.id}>
              {project.name}
              </div>
          )}
          </div>
        </header>


        <Footer />
      </div>

      // <Footer/>




    );
  }


}

export default App;

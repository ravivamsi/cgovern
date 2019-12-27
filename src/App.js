import React, {Component} from 'react';
import './App.css';
import axios from 'axios';


class App extends Component{

  state={
    isLoading: true,
    projects:[]
  }



  async componentDidMount(){


    const response = await axios.get("http://jsonplaceholder.typicode.com/users",
        { headers: {'Content-Type': 'application/json'}}
      );
      console.log(response);
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
      <div className="header" >
          <h3 className="text-muted" align="left">CGovern</h3>
      </div>

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
        <footer class="footer">
            <p>&copy; 2020 Vamsi Ravi</p>
        </footer>
      </div>




    );
  }


}

export default App;

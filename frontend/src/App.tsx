import React, {useEffect, useState} from "react";
import "./login.css";
import firebaseAuth from "./firebaseInit";
import {getAuth, GoogleAuthProvider, signInWithPopup} from "firebase/auth";

export default function FirebaseLoginUI() {

    let provider = new GoogleAuthProvider();


    const googleLogin = () => {
        console.log("Google login clicked");


        // const auth = getAuth();


        signInWithPopup(firebaseAuth, provider)
            .then(
                (result) => {

                    console.log("Google login successful", result);

                    // This gives you a Google Access Token. You can use it to access the Google API.
                    const credential = GoogleAuthProvider.credentialFromResult(result);
                    console.log("Credential:", credential);

                    const token = credential.accessToken;
                    console.log("Access Token:", token);

                    // The signed-in user info.
                    const user = result.user;
                    console.log("User Info:", user);


                    const displayName = user.displayName;
                    const email = user.email;
                    const photoURL = user.photoURL;
                    console.log("Name:", displayName, "Email:", email, "Photo URL:", photoURL);
                }
            ).catch((error) => {
            // Handle Errors here.
            const errorCode = error.code;
            const errorMessage = error.message;
            // The email of the user's account used.
            const email = error.customData.email;
            // The AuthCredential type that was used.
            const credential = GoogleAuthProvider.credentialFromError(error);
            // ...
        });
    }


  return (
      <div className="container">
        <div className="card">
          <h1 className="title">Firebase Login</h1>

          <div className="form-group">
            <input type="email" placeholder="Email" className="input" />
            <input type="password" placeholder="Password" className="input" />

            <button className="btn primary">Login</button>


            <button
                className="btn secondary"
                onClick={googleLogin}
            >
                Login with Google
            </button>


          </div>
        </div>
      </div>
  );
}

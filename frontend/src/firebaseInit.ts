// Import the functions you need from the SDKs you need
import { initializeApp } from "firebase/app";
import { getAuth } from "firebase/auth";

// TODO: Add SDKs for Firebase products that you want to use
// https://firebase.google.com/docs/web/setup#available-libraries

// Your web app's Firebase configuration
const firebaseConfig = {
  apiKey: "AIzaSyAXIyrPy0V7sJsHQRZIxcYvcWhkE7iRp2Q",
  authDomain: "bookstore-auth-1ffd0.firebaseapp.com",
  projectId: "bookstore-auth-1ffd0",
  storageBucket: "bookstore-auth-1ffd0.firebasestorage.app",
  messagingSenderId: "782809490226",
  appId: "1:782809490226:web:c3b5f6a6bcd96b1204ed2b",
  measurementId: "G-5PVCXLQYVN"
};

// Initialize Firebase
const firebaseApp = initializeApp(firebaseConfig);

const firebaseAuth = getAuth(firebaseApp);

export default  firebaseAuth;

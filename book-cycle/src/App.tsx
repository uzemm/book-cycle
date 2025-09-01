import { Routes, Route } from "react-router-dom";
import LoginPage from "@/pages/LoginPage";
import SignUpPage from "@/pages/SignUpPage";
import EmailVerificationPage from "@/pages/EmailVerificationPage";
import MainPage from "@/pages/MainPage";
import axios from "axios";
import { AuthProvider } from "@/pages/AuthContext";

axios.defaults.baseURL = "http://localhost:8080"; // 또는 실제 서버 주소

function App() {
  return (
    <AuthProvider>
      <Routes>
        <Route path="/main" element={<MainPage />} />
        <Route path="/login" element={<LoginPage />} />
        <Route path="/signup" element={<SignUpPage />} />
        <Route path="/verify" element={<EmailVerificationPage />} />
      </Routes>
    </AuthProvider>
  );
}

export default App;

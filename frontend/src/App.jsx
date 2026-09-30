import './App.css'
import { Navigate, Route, Routes } from 'react-router-dom'
import LoginPage from './pages/LoginPage'
import RegistroPage from './pages/RegistroPage'
import MeusAgendamentosPage from './pages/MeusAgendamentosPage'
import PrivateRoute from './components/PrivateRoute'
import Agendamentos from './pages/Agendamentos'
import Admin from './pages/Admin'

function App() {
  return (
    <Routes>
      <Route path="/login" element={<LoginPage />} />
      <Route path="/registro" element={<RegistroPage />} />
      <Route path="/agendamentos/meus" element={<PrivateRoute><MeusAgendamentosPage /></PrivateRoute>} />
      <Route path="/agendamentos" element={<PrivateRoute><Agendamentos /></PrivateRoute>} />
      <Route path="/admin" element={<PrivateRoute><Admin /></PrivateRoute>} />
      <Route path="*" element={<Navigate to="/login" replace />} />
    </Routes>
  )
}

export default App

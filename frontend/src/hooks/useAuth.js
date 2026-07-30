import { useContext } from 'react';
import { AuthContext } from '../contexts/AuthContext';

// Hook personalizado para facilitar o consumo do AuthContext
export function useAuth() {
    const context = useContext(AuthContext);

    if (!context) {
        throw new Error('useAuth deve ser usado dentro de um AuthProvider');
    }

    return context;
}

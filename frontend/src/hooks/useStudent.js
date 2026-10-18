import { useEffect, useState } from 'react'
import { getStudent } from '../api/students'

// vai buscar um estudante ao backend e devolve { student, loading, error }
// é usado no detalhe e na edição, que precisam de carregar o estudante da mesma forma
function useStudent(id) {
  // guarda também o id a que o resultado pertence
  // assim nunca se mistura o estudante anterior com o id novo
  const [result, setResult] = useState({ id: null, student: null, error: '' })

  useEffect(() => {
    // fica true se o id mudar (ou a página fechar) antes da resposta chegar
    // sem isto uma resposta atrasada do estudante 1 podia aparecer no estudante 2
    let ignore = false

    async function loadStudent() {
      try {
        const student = await getStudent(id)
        if (!ignore) setResult({ id, student, error: '' })
      } catch (e) {
        if (ignore) return
        // 404 é um estudante que não existe, qualquer outro erro é do servidor
        const error =
          e.status === 404
            ? 'Estudante não encontrado.'
            : 'Não foi possível carregar o estudante. Verifique se o backend está ligado.'
        setResult({ id, student: null, error })
      }
    }

    loadStudent()

    return () => {
      ignore = true
    }
  }, [id]) // corre outra vez sempre que o id mudar

  // enquanto o resultado guardado não for deste id, ainda estamos a carregar
  const ready = result.id === id

  return {
    student: ready ? result.student : null,
    error: ready ? result.error : '',
    loading: !ready,
  }
}

export default useStudent

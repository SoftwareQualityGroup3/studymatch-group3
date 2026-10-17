import { useEffect, useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { getStudent, updateStudent } from '../api/students'
import StudentForm from '../components/StudentForm'

function StudentEditPage() {
  // lê o id que vem no endereço tipo /students/4/edit
  const { id } = useParams()
  // serve para mudar de página por código
  const navigate = useNavigate()

  // estudante que vem do servidor para preencher o formulário
  const [student, setStudent] = useState(null)
  // fica true até o servidor responder
  const [loading, setLoading] = useState(true)
  // mensagem de erro se não conseguir carregar
  const [error, setError] = useState('')

  // corre quando a página abre e vai buscar o estudante
  useEffect(() => {
    async function loadStudent() {
      try {
        setStudent(await getStudent(id))
      } catch (e) {
        // 404 quer dizer que não existe qualquer outro erro é do servidor
        setError(
          e.status === 404
            ? 'Estudante não encontrado.'
            : 'Não foi possível carregar o estudante. Verifique se o backend está ligado.'
        )
      } finally {
        setLoading(false)
      }
    }

    loadStudent()
  }, [id]) // corre outra vez se o id mudar

  // o formulário chama isto quando se carrega em guardar
  async function handleSubmit(data) {
    // manda as alterações e espera
    // se o servidor der erro a função termina aqui e o erro volta para o formulário
    await updateStudent(id, data)

    // só chega aqui se correu bem e volta ao detalhe
    navigate(`/students/${id}`)
  }

  return (
    <section>
      <h2>Editar estudante</h2>

      {/* só aparece enquanto espera pela resposta */}
      {loading && <p>A carregar...</p>}

      {/* só aparece se não conseguiu carregar o estudante */}
      {error && <p role="alert">{error}</p>}

      {/* só mostra o formulário depois de ter os dados do estudante */}
      {student && (
        <StudentForm
          initialStudent={student}
          submitLabel="Guardar alterações"
          onSubmit={handleSubmit}
        />
      )}
    </section>
  )
}

export default StudentEditPage
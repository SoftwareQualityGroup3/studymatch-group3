import { useNavigate, useParams } from 'react-router-dom'
import { updateStudent } from '../api/students'
import StudentForm from '../components/StudentForm'
import useStudent from '../hooks/useStudent'

function StudentEditPage() {
  // lê o id que vem no endereço, tipo /students/4/edit
  const { id } = useParams()
  // serve para mudar de página por código
  const navigate = useNavigate()

  // vai buscar o estudante para preencher o formulário
  const { student, loading, error } = useStudent(id)

  // o formulário chama isto quando se carrega em guardar
  async function handleSubmit(data) {
    // se o backend der erro, o await lança e o erro volta para o formulário
    await updateStudent(id, data)

    // só chega aqui se correu bem, e volta ao detalhe
    navigate(`/students/${id}`)
  }

  return (
    <section>
      <h2>Editar estudante</h2>

      {/* só aparece enquanto espera pela resposta */}
      {loading && <p>A carregar...</p>}

      {/* só aparece se não conseguiu carregar o estudante */}
      {error && <p role="alert">{error}</p>}

      {/* o formulário só aparece quando já temos os dados deste estudante
          a key obriga a criar um formulário novo se mudarmos de estudante,
          senão ficavam as caixas com os dados do anterior */}
      {student && (
        <StudentForm
          key={student.id}
          initialStudent={student}
          submitLabel="Guardar alterações"
          onSubmit={handleSubmit}
        />
      )}
    </section>
  )
}

export default StudentEditPage

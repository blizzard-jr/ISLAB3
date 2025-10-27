<template>
  <div class="min-h-screen bg-gray-50 p-6">
    <div class="max-w-7xl mx-auto">
      <div class="bg-white rounded-lg shadow-sm p-6">
        <div class="flex justify-between items-center mb-6">
          <h1 class="text-3xl font-bold text-gray-900">Управление BookCreature</h1>
          <button
            @click="openCreateDialog"
            class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors flex items-center gap-2"
          >
            <Plus :size="20" />
            Создать объект
          </button>
        </div>

         Фильтры и поиск 
        <div class="mb-6 flex gap-4">
          <div class="flex-1">
            <input
              v-model="searchQuery"
              @input="handleSearch"
              type="text"
              placeholder="Поиск по имени..."
              class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
            />
          </div>
          <select
            v-model="sortField"
            @change="handleSort"
            class="px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
          >
            <option value="">Сортировка</option>
            <option value="id">По ID</option>
            <option value="name">По имени</option>
            <option value="age">По возрасту</option>
            <option value="creatureType">По типу</option>
            <option value="attackLevel">По атаке</option>
            <option value="defenseLevel">По защите</option>
            <option value="creationDate">По дате создания</option>
          </select>
        </div>

         Таблица 
        <div class="overflow-x-auto">
          <table class="w-full">
            <thead class="bg-gray-100 border-b border-gray-200">
              <tr>
                <th class="px-4 py-3 text-left text-sm font-semibold text-gray-700">ID</th>
                <th class="px-4 py-3 text-left text-sm font-semibold text-gray-700">Имя</th>
                <th class="px-4 py-3 text-left text-sm font-semibold text-gray-700">Возраст</th>
                <th class="px-4 py-3 text-left text-sm font-semibold text-gray-700">Тип</th>
                <th class="px-4 py-3 text-left text-sm font-semibold text-gray-700">Координаты</th>
                <th class="px-4 py-3 text-left text-sm font-semibold text-gray-700">Атака</th>
                <th class="px-4 py-3 text-left text-sm font-semibold text-gray-700">Защита</th>
                <th class="px-4 py-3 text-left text-sm font-semibold text-gray-700">Дата создания</th>
                <th class="px-4 py-3 text-left text-sm font-semibold text-gray-700">Действия</th>
              </tr>
            </thead>
            <tbody class="divide-y divide-gray-200">
              <tr
                v-for="creature in creatures"
                :key="creature.id"
                class="hover:bg-gray-50 transition-colors"
              >
                <td class="px-4 py-3 text-sm text-gray-900">{{ creature.id }}</td>
                <td class="px-4 py-3 text-sm text-gray-900 font-medium">{{ creature.name }}</td>
                <td class="px-4 py-3 text-sm text-gray-600">{{ creature.age }}</td>
                <td class="px-4 py-3 text-sm">
                  <span class="px-2 py-1 bg-blue-100 text-blue-800 rounded-full text-xs">
                    {{ creature.creatureType }}
                  </span>
                </td>
                <td class="px-4 py-3 text-sm text-gray-600">
                  ({{ creature.coordinates.x }}, {{ creature.coordinates.y.toFixed(2) }})
                </td>
                <td class="px-4 py-3 text-sm text-gray-600">
                  {{ creature.attackLevel ?? 'N/A' }}
                </td>
                <td class="px-4 py-3 text-sm text-gray-600">{{ creature.defenseLevel }}</td>
                <td class="px-4 py-3 text-sm text-gray-600">
                  {{ formatDate(creature.creationDate) }}
                </td>
                <td class="px-4 py-3 text-sm">
                  <div class="flex gap-2">
                    <button
                      @click="viewCreature(creature.id)"
                      class="p-1 text-blue-600 hover:bg-blue-50 rounded"
                      title="Просмотр"
                    >
                      <Eye :size="18" />
                    </button>
                    <button
                      @click="editCreature(creature.id)"
                      class="p-1 text-green-600 hover:bg-green-50 rounded"
                      title="Редактировать"
                    >
                      <Edit :size="18" />
                    </button>
                    <button
                      @click="deleteCreature(creature.id)"
                      class="p-1 text-red-600 hover:bg-red-50 rounded"
                      title="Удалить"
                    >
                      <Trash2 :size="18" />
                    </button>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>

        <div class="mt-6 flex justify-between items-center">
          <div class="text-sm text-gray-600">
            Показано {{ (currentPage - 1) * pageSize + 1 }}-{{ Math.min(currentPage * pageSize, totalItems) }} из {{ totalItems }}
          </div>
          <div class="flex gap-2">
            <button
              @click="changePage(currentPage - 1)"
              :disabled="currentPage === 1"
              class="px-4 py-2 border border-gray-300 rounded-lg hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed"
            >
              <ChevronLeft :size="20" />
            </button>
            <span class="px-4 py-2 border border-gray-300 rounded-lg bg-blue-50 text-blue-700">
              {{ currentPage }}
            </span>
            <button
              @click="changePage(currentPage + 1)"
              :disabled="currentPage >= totalPages"
              class="px-4 py-2 border border-gray-300 rounded-lg hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed"
            >
              <ChevronRight :size="20" />
            </button>
          </div>
        </div>
      </div>
    </div>

    <CreateMenuDialog
      v-if="showCreateMenu"
      @close="showCreateMenu = false"
      @create-creature="openCreatureDialog"
      @create-ring="openRingDialog"
      @create-city="openCityDialog"
    />

    <CreatureDialog
      v-if="showCreatureDialog"
      :creature-id="selectedCreatureId"
      :mode="dialogMode"
      @close="closeCreatureDialog"
      @saved="handleSaved"
    />

    <RingDialog
      v-if="showRingDialog"
      :ring-id="selectedRingId"
      :mode="ringDialogMode"
      @close="closeRingDialog"
      @saved="handleSaved"
    />

    <CityDialog
      v-if="showCityDialog"
      :city-id="selectedCityId"
      :mode="cityDialogMode"
      @close="closeCityDialog"
      @saved="handleSaved"
    />

    <ViewDialog
      v-if="showViewDialog"
      :creature-id="selectedCreatureId"
      @close="showViewDialog = false"
      @edit="editFromView"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { Plus, Eye, Edit, Trash2, ChevronLeft, ChevronRight } from 'lucide-vue-next'
import CreateMenuDialog from './CreateMenuDialog.vue'
import CreatureDialog from './CreatureDialog.vue'
import RingDialog from './RingDialog.vue'
import CityDialog from './CityDialog.vue'
import ViewDialog from './ViewDialog.vue'
import { api } from './api.js'
import { showApiError, showApiSuccess } from './utils/errorHandler.js'

interface Coordinates {
  x: number
  y: number
}

interface MagicCity {
  name: string
  area: number
  population: number
  establishmentDate: string
  governor: string
  capital: boolean
  populationDensity: number
}

interface Ring {
  name: string
  power: number
  weight: number
}

interface BookCreature {
  id: number
  name: string
  coordinates: Coordinates
  creationDate: string
  age: number
  creatureType: 'HOBBIT' | 'ELF' | 'HUMAN' | 'GOLLUM'
  creatureLocation: MagicCity | null
  attackLevel: number | null
  defenseLevel: number
  ring: Ring | null
}

const creatures = ref<BookCreature[]>([])
const currentPage = ref(1)
const pageSize = ref(10)
const totalItems = ref(0)
const totalPages = ref(0)
const searchQuery = ref('')
const sortField = ref('')

const showCreateMenu = ref(false)
const showCreatureDialog = ref(false)
const showRingDialog = ref(false)
const showCityDialog = ref(false)
const showViewDialog = ref(false)

const selectedCreatureId = ref<number | null>(null)
const selectedRingId = ref<number | null>(null)
const selectedCityId = ref<number | null>(null)

const dialogMode = ref<'create' | 'edit'>('create')
const ringDialogMode = ref<'create' | 'edit'>('create')
const cityDialogMode = ref<'create' | 'edit'>('create')

let websocket: WebSocket | null = null

async function fetchCreatures() {
  try {
    const data = await api.getAllCreatures({
      page: currentPage.value,
      size: pageSize.value,
      search: searchQuery.value,
      sort: sortField.value
    })
    
    creatures.value = data.content
    totalItems.value = data.totalElements
    totalPages.value = data.totalPages
  } catch (error) {
    console.error('Ошибка загрузки объектов:', error)
    showApiError({
      title: 'Ошибка загрузки',
      message: 'Не удалось загрузить данные с сервера'
    })
  }
}

async function deleteCreature(id: number) {
  if (!confirm('Вы уверены, что хотите удалить этот объект? Связанные объекты также будут удалены.')) {
    return
  }
  
  try {
    await api.deleteCreature(id)
    showApiSuccess('Объект успешно удален!')
    await fetchCreatures()
  } catch (error) {
    if (error.status && error.title) {
      showApiError(error)
    } else {
      showApiError({
        title: 'Ошибка удаления',
        message: error.message || 'Не удалось удалить объект'
      })
    }
  }
}

function openCreateDialog() {
  showCreateMenu.value = true
}

function openCreatureDialog() {
  showCreateMenu.value = false
  dialogMode.value = 'create'
  selectedCreatureId.value = null
  showCreatureDialog.value = true
}

function openRingDialog() {
  showCreateMenu.value = false
  ringDialogMode.value = 'create'
  selectedRingId.value = null
  showRingDialog.value = true
}

function openCityDialog() {
  showCreateMenu.value = false
  cityDialogMode.value = 'create'
  selectedCityId.value = null
  showCityDialog.value = true
}

function editCreature(id: number) {
  dialogMode.value = 'edit'
  selectedCreatureId.value = id
  showCreatureDialog.value = true
}

function viewCreature(id: number) {
  selectedCreatureId.value = id
  showViewDialog.value = true
}

function editFromView(id: number) {
  showViewDialog.value = false
  editCreature(id)
}

function closeCreatureDialog() {
  showCreatureDialog.value = false
  selectedCreatureId.value = null
}

function closeRingDialog() {
  showRingDialog.value = false
  selectedRingId.value = null
}

function closeCityDialog() {
  showCityDialog.value = false
  selectedCityId.value = null
}

function handleSaved() {
  closeCreatureDialog()
  closeRingDialog()
  closeCityDialog()
  fetchCreatures()
}

function changePage(page: number) {
  currentPage.value = page
  fetchCreatures()
}

function handleSearch() {
  currentPage.value = 1
  fetchCreatures()
}

function handleSort() {
  fetchCreatures()
}

function formatDate(dateString: string): string {
  if (!dateString) return 'N/A'
  const date = new Date(dateString)
  return date.toLocaleString('ru-RU', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

function connectWebSocket() {
  console.log('WebSocket подключение (заглушка)')
}

onMounted(() => {
  fetchCreatures()
  connectWebSocket()
})

onUnmounted(() => {
  if (websocket) {
    websocket.close()
  }
})
</script>
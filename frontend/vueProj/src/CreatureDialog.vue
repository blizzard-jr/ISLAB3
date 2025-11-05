<template>
  <div class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
    <div class="bg-white rounded-lg shadow-xl max-w-3xl w-full max-h-[90vh] overflow-y-auto">
      <div class="sticky top-0 bg-white border-b border-gray-200 px-6 py-4 flex justify-between items-center">
        <h2 class="text-2xl font-bold text-gray-900">
          {{ mode === 'create' ? 'Создать новый объект' : 'Редактировать объект' }}
        </h2>
        <button @click="$emit('close')" class="text-gray-500 hover:text-gray-700">
          <X :size="24" />
        </button>
      </div>

      <form @submit.prevent="handleSubmit" class="p-6 space-y-6">
        <!-- Основная информация -->
        <div class="space-y-4">
          <h3 class="text-lg font-semibold text-gray-900">Основная информация</h3>
          
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">
              Имя <span class="text-red-500">*</span>
            </label>
            <input
              v-model="formData.name"
              type="text"
              required
              class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
            />
          </div>

          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">
              Возраст <span class="text-red-500">*</span>
            </label>
            <input
              v-model.number="formData.age"
              type="number"
              required
              min="1"
              class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
            />
          </div>

          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">
              Тип существа <span class="text-red-500">*</span>
            </label>
            <select
              v-model="formData.creatureType"
              required
              class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
            >
              <option value="">Выберите тип</option>
              <option value="HOBBIT">Хоббит</option>
              <option value="ELF">Эльф</option>
              <option value="HUMAN">Человек</option>
              <option value="GOLLUM">Голлум</option>
            </select>
          </div>
        </div>

        <!-- Координаты -->
        <div class="space-y-4">
          <h3 class="text-lg font-semibold text-gray-900">Координаты</h3>
          
          <div class="grid grid-cols-2 gap-4">
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">
                X <span class="text-red-500">*</span>
              </label>
              <input
                v-model.number="formData.coordinates.x"
                type="number"
                required
                class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
              />
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">
                Y <span class="text-red-500">*</span>
              </label>
              <input
                v-model.number="formData.coordinates.y"
                type="number"
                step="0.01"
                required
                max="738"
                class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
              />
            </div>
          </div>
        </div>

        <!-- Характеристики -->
        <div class="space-y-4">
          <h3 class="text-lg font-semibold text-gray-900">Характеристики</h3>
          
          <div class="grid grid-cols-2 gap-4">
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">
                Уровень атаки
              </label>
              <input
                v-model.number="formData.attackLevel"
                type="number"
                step="0.1"
                class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
              />
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">
                Уровень защиты <span class="text-red-500">*</span>
              </label>
              <input
                v-model.number="formData.defenseLevel"
                type="number"
                step="0.1"
                required
                class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
              />
            </div>
          </div>
        </div>

        <!-- Локация -->
        <div class="space-y-4">
          <div class="flex items-center justify-between">
            <h3 class="text-lg font-semibold text-gray-900">Локация</h3>
            <label class="flex items-center gap-2">
              <input
                v-model="hasLocation"
                type="checkbox"
                class="rounded border-gray-300"
              />
              <span class="text-sm text-gray-700">Добавить локацию</span>
            </label>
          </div>
          
          <div v-if="hasLocation" class="space-y-4 pl-4 border-l-2 border-blue-200">
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-2">
                Выберите город
              </label>
              <div class="mb-2">
                <label class="flex items-center gap-2 mb-2">
                  <input
                    v-model="selectExistingCity"
                    type="radio"
                    :value="true"
                    @change="loadCities(1)"
                    class="rounded border-gray-300"
                  />
                  <span class="text-sm text-gray-700">Выбрать существующий город</span>
                </label>
                <label class="flex items-center gap-2">
                  <input
                    v-model="selectExistingCity"
                    type="radio"
                    :value="false"
                    class="rounded border-gray-300"
                  />
                  <span class="text-sm text-gray-700">Создать новый город</span>
                </label>
              </div>
              
              <div v-if="selectExistingCity" class="space-y-2">
                <select
                  v-model="selectedCityId"
                  class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
                  @change="onCitySelected"
                >
                  <option :value="null">Выберите город</option>
                  <option
                    v-for="city in cities"
                    :key="city.id"
                    :value="city.id"
                  >
                    {{ city.name }} ({{ city.population }} жителей)
                  </option>
                </select>
                
                <!-- Пагинация для городов -->
                <div v-if="citiesTotalPages > 1" class="flex items-center justify-between mt-2">
                  <div class="text-xs text-gray-600">
                    Страница {{ citiesCurrentPage }} из {{ citiesTotalPages }}
                  </div>
                  <div class="flex gap-1">
                    <button
                      type="button"
                      @click="changeCitiesPage(citiesCurrentPage - 1)"
                      :disabled="citiesCurrentPage === 1"
                      class="px-2 py-1 text-xs border border-gray-300 rounded hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed"
                    >
                      ←
                    </button>
                    <span class="px-2 py-1 text-xs border border-gray-300 rounded bg-blue-50">
                      {{ citiesCurrentPage }}
                    </span>
                    <button
                      type="button"
                      @click="changeCitiesPage(citiesCurrentPage + 1)"
                      :disabled="citiesCurrentPage >= citiesTotalPages"
                      class="px-2 py-1 text-xs border border-gray-300 rounded hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed"
                    >
                      →
                    </button>
                  </div>
                </div>
              </div>
            </div>
            
            <div v-if="!selectExistingCity" class="space-y-4">
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">
                Название <span class="text-red-500">*</span>
              </label>
              <input
                v-model="formData.creatureLocation.name"
                type="text"
                :required="hasLocation"
                class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
              />
            </div>

            <div class="grid grid-cols-2 gap-4">
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">
                  Площадь <span class="text-red-500">*</span>
                </label>
                <input
                  v-model.number="formData.creatureLocation.area"
                  type="number"
                  min="1"
                  :required="hasLocation"
                  class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
                />
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">
                  Население <span class="text-red-500">*</span>
                </label>
                <input
                  v-model.number="formData.creatureLocation.population"
                  type="number"
                  min="1"
                  :required="hasLocation"
                  class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
                />
              </div>
            </div>

            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">
                Дата основания <span class="text-red-500">*</span>
              </label>
              <input
                v-model="formData.creatureLocation.establishmentDate"
                type="datetime-local"
                :required="hasLocation"
                class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
              />
            </div>

            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">
                Правитель <span class="text-red-500">*</span>
              </label>
              <select
                v-model="formData.creatureLocation.governor"
                :required="hasLocation"
                class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
              >
                <option value="">Выберите тип правителя</option>
                <option value="HOBBIT">Хоббит</option>
                <option value="ELF">Эльф</option>
                <option value="HUMAN">Человек</option>
                <option value="GOLLUM">Голлум</option>
              </select>
            </div>

            <label class="flex items-center gap-2">
              <input
                v-model="formData.creatureLocation.capital"
                type="checkbox"
                class="rounded border-gray-300"
              />
              <span class="text-sm text-gray-700">Столица</span>
            </label>

            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">
                Плотность населения <span class="text-red-500">*</span>
              </label>
              <input
                v-model.number="formData.creatureLocation.populationDensity"
                type="number"
                step="0.1"
                :required="hasLocation"
                class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
              />
            </div>
            </div>
          </div>
        </div>

        <!-- Кольцо -->
        <div class="space-y-4">
          <div class="flex items-center justify-between">
            <h3 class="text-lg font-semibold text-gray-900">Кольцо</h3>
            <label class="flex items-center gap-2">
              <input
                v-model="hasRing"
                type="checkbox"
                class="rounded border-gray-300"
              />
              <span class="text-sm text-gray-700">Добавить кольцо</span>
            </label>
          </div>
          
          <div v-if="hasRing" class="space-y-4 pl-4 border-l-2 border-yellow-200">
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-2">
                Выберите кольцо
              </label>
              <div class="mb-2">
                <label class="flex items-center gap-2 mb-2">
                  <input
                    v-model="selectExistingRing"
                    type="radio"
                    :value="true"
                    @change="loadRings(1)"
                    class="rounded border-gray-300"
                  />
                  <span class="text-sm text-gray-700">Выбрать существующее кольцо</span>
                </label>
                <label class="flex items-center gap-2">
                  <input
                    v-model="selectExistingRing"
                    type="radio"
                    :value="false"
                    class="rounded border-gray-300"
                  />
                  <span class="text-sm text-gray-700">Создать новое кольцо</span>
                </label>
              </div>
              
              <div v-if="selectExistingRing" class="space-y-2">
                <select
                  v-model="selectedRingId"
                  class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
                  @change="onRingSelected"
                >
                  <option :value="null">Выберите кольцо</option>
                  <option
                    v-for="ring in rings"
                    :key="ring.id"
                    :value="ring.id"
                  >
                    {{ ring.name }} (Сила: {{ ring.power }}, Вес: {{ ring.weight }})
                  </option>
                </select>
                
                <!-- Пагинация для колец -->
                <div v-if="ringsTotalPages > 1" class="flex items-center justify-between mt-2">
                  <div class="text-xs text-gray-600">
                    Страница {{ ringsCurrentPage }} из {{ ringsTotalPages }}
                  </div>
                  <div class="flex gap-1">
                    <button
                      type="button"
                      @click="changeRingsPage(ringsCurrentPage - 1)"
                      :disabled="ringsCurrentPage === 1"
                      class="px-2 py-1 text-xs border border-gray-300 rounded hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed"
                    >
                      ←
                    </button>
                    <span class="px-2 py-1 text-xs border border-gray-300 rounded bg-blue-50">
                      {{ ringsCurrentPage }}
                    </span>
                    <button
                      type="button"
                      @click="changeRingsPage(ringsCurrentPage + 1)"
                      :disabled="ringsCurrentPage >= ringsTotalPages"
                      class="px-2 py-1 text-xs border border-gray-300 rounded hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed"
                    >
                      →
                    </button>
                  </div>
                </div>
              </div>
            </div>
            
            <div v-if="!selectExistingRing" class="space-y-4">
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">
                Название <span class="text-red-500">*</span>
              </label>
              <input
                v-model="formData.ring.name"
                type="text"
                :required="hasRing"
                class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
              />
            </div>

            <div class="grid grid-cols-2 gap-4">
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">
                  Сила <span class="text-red-500">*</span>
                </label>
                <input
                  v-model.number="formData.ring.power"
                  type="number"
                  :required="hasRing"
                  class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
                />
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">
                  Вес <span class="text-red-500">*</span>
                </label>
                <input
                  v-model.number="formData.ring.weight"
                  type="number"
                  step="0.01"
                  :required="hasRing"
                  class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
                />
              </div>
            </div>
            </div>
          </div>
        </div>

        <!-- Кнопки -->
        <div class="flex justify-end gap-3 pt-4 border-t border-gray-200">
          <button
            type="button"
            @click="$emit('close')"
            class="px-6 py-2 border border-gray-300 rounded-lg hover:bg-gray-50 transition-colors"
          >
            Отмена
          </button>
          <button
            type="submit"
            class="px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors flex items-center gap-2"
          >
            <Save :size="18" />
            {{ mode === 'create' ? 'Создать' : 'Сохранить' }}
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import { X, Save } from 'lucide-vue-next'
import { api } from './api.js'

const props = defineProps<{
  creatureId: number | null
  mode: 'create' | 'edit'
}>()

const emit = defineEmits<{
  close: []
  saved: []
}>()

const hasLocation = ref(false)
const hasRing = ref(false)
const selectExistingCity = ref(false)
const selectExistingRing = ref(false)

// Состояние для городов
const cities = ref([])
const citiesCurrentPage = ref(1)
const citiesTotalPages = ref(0)
const selectedCityId = ref<number | null>(null)

// Состояние для колец
const rings = ref([])
const ringsCurrentPage = ref(1)
const ringsTotalPages = ref(0)
const selectedRingId = ref<number | null>(null)

const formData = ref({
  name: '',
  age: null as number | null,
  creatureType: '',
  coordinates: {
    x: null as number | null,
    y: null as number | null
  },
  attackLevel: null as number | null,
  defenseLevel: null as number | null,
  creatureLocation: {
    name: '',
    area: null as number | null,
    population: null as number | null,
    establishmentDate: '',
    governor: '',
    capital: false,
    populationDensity: null as number | null
  },
  ring: {
    name: '',
    power: null as number | null,
    weight: null as number | null
  }
})

watch(hasLocation, (newValue) => {
  if (!newValue) {
    formData.value.creatureLocation = {
      name: '',
      area: null,
      population: null,
      establishmentDate: '',
      governor: '',
      capital: false,
      populationDensity: null
    }
  }
})

watch(hasRing, (newValue) => {
  if (!newValue) {
    formData.value.ring = {
      name: '',
      power: null,
      weight: null
    }
    selectedRingId.value = null
    selectExistingRing.value = false
  }
})

watch(hasLocation, (newValue) => {
  if (!newValue) {
    selectedCityId.value = null
    selectExistingCity.value = false
  }
})

// Загрузка городов
async function loadCities(page = 1) {
  try {
    const data = await api.getCities(page)
    cities.value = data.content
    citiesCurrentPage.value = data.currentPage
    citiesTotalPages.value = data.totalPages
  } catch (error) {
    console.error('Ошибка загрузки городов:', error)
    alert('Ошибка при загрузке городов')
  }
}

// Загрузка колец
async function loadRings(page = 1) {
  try {
    const data = await api.getRings(page)
    rings.value = data.content
    ringsCurrentPage.value = data.currentPage
    ringsTotalPages.value = data.totalPages
  } catch (error) {
    console.error('Ошибка загрузки колец:', error)
    alert('Ошибка при загрузке колец')
  }
}

// Смена страницы городов
function changeCitiesPage(page: number) {
  citiesCurrentPage.value = page
  loadCities(page)
}

// Смена страницы колец
function changeRingsPage(page: number) {
  ringsCurrentPage.value = page
  loadRings(page)
}

// Обработка выбора города
function onCitySelected() {
  if (selectedCityId.value) {
    // Можно загрузить полные данные города если нужно
    // Пока просто используем id
  }
}

// Обработка выбора кольца
function onRingSelected() {
  if (selectedRingId.value) {
    // Можно загрузить полные данные кольца если нужно
    // Пока просто используем id
  }
}

async function loadCreature() {
  if (!props.creatureId) return

  try {
    const data = await api.getCreatureById(props.creatureId)
    
    formData.value = {
      ...data,
      creatureLocation: data.creatureLocation || {
        name: '',
        area: null,
        population: null,
        establishmentDate: '',
        governor: '',
        capital: false,
        populationDensity: null
      },
      ring: data.ring || {
        name: '',
        power: null,
        weight: null
      }
    }

    hasLocation.value = !!data.creatureLocation
    hasRing.value = !!data.ring
    
    // Если при редактировании уже есть город/кольцо, устанавливаем их id
    if (data.creatureLocation?.id) {
      selectedCityId.value = data.creatureLocation.id
      selectExistingCity.value = true
      // Загружаем список городов и ищем нужную страницу
      await loadCities(1)
      // Если выбранный город не на первой странице, нужно найти его страницу
      // Пока просто загружаем первую страницу, пользователь сможет найти нужный город
    }
    
    if (data.ring?.id) {
      selectedRingId.value = data.ring.id
      selectExistingRing.value = true
      // Загружаем список колец
      await loadRings(1)
    }
  } catch (error) {
    console.error('Ошибка загрузки объекта:', error)
    alert('Ошибка при загрузке данных объекта')
  }
}

async function handleSubmit() {
  let creatureLocation = null
  let ring = null
  
  // Формируем данные локации
  if (hasLocation.value) {
    if (selectExistingCity.value && selectedCityId.value) {
      // Используем существующий город - передаем только id
      creatureLocation = { id: selectedCityId.value }
    } else {
      // Создаем новый город - передаем все данные
      creatureLocation = formData.value.creatureLocation
    }
  }
  
  // Формируем данные кольца
  if (hasRing.value) {
    if (selectExistingRing.value && selectedRingId.value) {
      // Используем существующее кольцо - передаем только id
      ring = { id: selectedRingId.value }
    } else {
      // Создаем новое кольцо - передаем все данные
      ring = formData.value.ring
    }
  }
  
  const submitData = {
    ...formData.value,
    creatureLocation,
    ring
  }

  try {
    if (props.mode === 'create') {
      await api.createCreature(submitData)
      alert('Объект успешно создан!')
    } else {
      await api.updateCreature(props.creatureId, submitData)
      alert('Объект успешно обновлен!')
    }

    emit('saved')
  } catch (error) {
    console.error('Ошибка при сохранении:', error)
    alert('Ошибка при сохранении объекта: ' + error.message)
  }
}

onMounted(() => {
  if (props.mode === 'edit') {
    loadCreature()
  }
})
</script>

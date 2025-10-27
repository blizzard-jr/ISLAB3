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
              <input
                v-model="formData.creatureLocation.governor"
                type="text"
                :required="hasLocation"
                class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
              />
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
  }
})

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
  } catch (error) {
    console.error('Ошибка загрузки объекта:', error)
    alert('Ошибка при загрузке данных объекта')
  }
}

async function handleSubmit() {
  const submitData = {
    ...formData.value,
    creatureLocation: hasLocation.value ? formData.value.creatureLocation : null,
    ring: hasRing.value ? formData.value.ring : null
  }

  try {
    if (props.mode === 'create') {
      await api.createCreature(submitData)
      alert('Объект успешно создан!')
    } else {
      await api.updateCreature(props.creatureId, submitData)
      alert('Объект успешно обновлен!')
    }
    await api.getAllCreatures()

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

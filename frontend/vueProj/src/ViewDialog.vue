<template>
  <div class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
    <div class="bg-white rounded-lg shadow-xl max-w-3xl w-full max-h-[90vh] overflow-y-auto">
      <div class="sticky top-0 bg-white border-b border-gray-200 px-6 py-4 flex justify-between items-center">
        <h2 class="text-2xl font-bold text-gray-900">Информация об объекте</h2>
        <button @click="$emit('close')" class="text-gray-500 hover:text-gray-700">
          <X :size="24" />
        </button>
      </div>

      <div v-if="creature" class="p-6 space-y-6">
         Основная информация 
        <div class="bg-gray-50 rounded-lg p-4 space-y-3">
          <h3 class="text-lg font-semibold text-gray-900 mb-3">Основная информация</h3>
          <div class="grid grid-cols-2 gap-4">
            <div>
              <span class="text-sm text-gray-600">ID:</span>
              <p class="font-medium">{{ creature.id }}</p>
            </div>
            <div>
              <span class="text-sm text-gray-600">Имя:</span>
              <p class="font-medium">{{ creature.name }}</p>
            </div>
            <div>
              <span class="text-sm text-gray-600">Возраст:</span>
              <p class="font-medium">{{ creature.age }}</p>
            </div>
            <div>
              <span class="text-sm text-gray-600">Тип существа:</span>
              <p class="font-medium">
                <span class="px-2 py-1 bg-blue-100 text-blue-800 rounded-full text-sm">
                  {{ creature.creatureType }}
                </span>
              </p>
            </div>
            <div>
              <span class="text-sm text-gray-600">Дата создания:</span>
              <p class="font-medium">{{ formatDate(creature.creationDate) }}</p>
            </div>
          </div>
        </div>

         Координаты 
        <div class="bg-gray-50 rounded-lg p-4 space-y-3">
          <h3 class="text-lg font-semibold text-gray-900 mb-3">Координаты</h3>
          <div class="grid grid-cols-2 gap-4">
            <div>
              <span class="text-sm text-gray-600">X:</span>
              <p class="font-medium">{{ creature.coordinates.x }}</p>
            </div>
            <div>
              <span class="text-sm text-gray-600">Y:</span>
              <p class="font-medium">{{ creature.coordinates.y.toFixed(2) }}</p>
            </div>
          </div>
        </div>

         Характеристики 
        <div class="bg-gray-50 rounded-lg p-4 space-y-3">
          <h3 class="text-lg font-semibold text-gray-900 mb-3">Характеристики</h3>
          <div class="grid grid-cols-2 gap-4">
            <div>
              <span class="text-sm text-gray-600">Уровень атаки:</span>
              <p class="font-medium">{{ creature.attackLevel ?? 'Не указан' }}</p>
            </div>
            <div>
              <span class="text-sm text-gray-600">Уровень защиты:</span>
              <p class="font-medium">{{ creature.defenseLevel }}</p>
            </div>
          </div>
        </div>

         Локация 
        <div v-if="creature.creatureLocation" class="bg-gray-50 rounded-lg p-4 space-y-3">
          <h3 class="text-lg font-semibold text-gray-900 mb-3">Локация</h3>
          <div class="grid grid-cols-2 gap-4">
            <div>
              <span class="text-sm text-gray-600">Название:</span>
              <p class="font-medium">{{ creature.creatureLocation.name }}</p>
            </div>
            <div>
              <span class="text-sm text-gray-600">Площадь:</span>
              <p class="font-medium">{{ creature.creatureLocation.area }}</p>
            </div>
            <div>
              <span class="text-sm text-gray-600">Население:</span>
              <p class="font-medium">{{ creature.creatureLocation.population }}</p>
            </div>
            <div>
              <span class="text-sm text-gray-600">Плотность населения:</span>
              <p class="font-medium">{{ creature.creatureLocation.populationDensity }}</p>
            </div>
            <div>
              <span class="text-sm text-gray-600">Дата основания:</span>
              <p class="font-medium">{{ formatDate(creature.creatureLocation.establishmentDate) }}</p>
            </div>
            <div>
              <span class="text-sm text-gray-600">Правитель:</span>
              <p class="font-medium">{{ creature.creatureLocation.governor }}</p>
            </div>
            <div>
              <span class="text-sm text-gray-600">Столица:</span>
              <p class="font-medium">{{ creature.creatureLocation.capital ? 'Да' : 'Нет' }}</p>
            </div>
          </div>
        </div>
        <div v-else class="bg-gray-50 rounded-lg p-4">
          <p class="text-gray-600">Локация не указана</p>
        </div>

         Кольцо 
        <div v-if="creature.ring" class="bg-gray-50 rounded-lg p-4 space-y-3">
          <h3 class="text-lg font-semibold text-gray-900 mb-3">Кольцо</h3>
          <div class="grid grid-cols-3 gap-4">
            <div>
              <span class="text-sm text-gray-600">Название:</span>
              <p class="font-medium">{{ creature.ring.name }}</p>
            </div>
            <div>
              <span class="text-sm text-gray-600">Сила:</span>
              <p class="font-medium">{{ creature.ring.power }}</p>
            </div>
            <div>
              <span class="text-sm text-gray-600">Вес:</span>
              <p class="font-medium">{{ creature.ring.weight }}</p>
            </div>
          </div>
        </div>
        <div v-else class="bg-gray-50 rounded-lg p-4">
          <p class="text-gray-600">Кольцо отсутствует</p>
        </div>

         Кнопки действий 
        <div class="flex justify-end gap-3 pt-4 border-t border-gray-200">
          <button
            @click="$emit('close')"
            class="px-6 py-2 border border-gray-300 rounded-lg hover:bg-gray-50 transition-colors"
          >
            Закрыть
          </button>
          <button
            @click="$emit('edit', creatureId)"
            class="px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors flex items-center gap-2"
          >
            <Edit :size="18" />
            Редактировать
          </button>
        </div>
      </div>

      <div v-else class="p-6">
        <p class="text-center text-gray-600">Загрузка...</p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { X, Edit } from 'lucide-vue-next'
import { api } from './api.js'

const props = defineProps<{
  creatureId: number
}>()

defineEmits<{
  close: []
  edit: [id: number]
}>()

const creature = ref<any>(null)

function formatDate(dateString: string): string {
  const date = new Date(dateString)
  return date.toLocaleString('ru-RU')
}

async function loadCreature() {
  try {
    creature.value = await api.getCreatureById(props.creatureId)
  } catch (error) {
    console.error('Ошибка загрузки объекта:', error)
    alert('Ошибка при загрузке данных объекта')
  }
}

onMounted(() => {
  loadCreature()
})
</script>
<template>
  <div class="fixed top-4 right-4 z-[9999] space-y-2">
    <TransitionGroup name="toast">
      <div
        v-for="toast in toasts"
        :key="toast.id"
        :class="[
          'min-w-[300px] max-w-md px-4 py-3 rounded-lg shadow-lg flex items-start gap-3',
          'transform transition-all duration-300',
          toastClasses[toast.type]
        ]"
      >
        <component :is="toastIcons[toast.type]" :size="20" class="flex-shrink-0 mt-0.5" />
        <div class="flex-1">
          <p class="text-sm font-medium" v-html="toast.message"></p>
        </div>
        <button
          @click="removeToast(toast.id)"
          class="flex-shrink-0 opacity-70 hover:opacity-100 transition-opacity"
        >
          <X :size="18" />
        </button>
      </div>
    </TransitionGroup>
  </div>
</template>

<script setup>
import { CheckCircle, AlertCircle, AlertTriangle, Info, X } from 'lucide-vue-next'
import { useToast } from '../composables/useToast.js'

const { toasts, removeToast } = useToast()

const toastClasses = {
  success: 'bg-green-50 border border-green-200 text-green-800',
  error: 'bg-red-50 border border-red-200 text-red-800',
  warning: 'bg-yellow-50 border border-yellow-200 text-yellow-800',
  info: 'bg-blue-50 border border-blue-200 text-blue-800'
}

const toastIcons = {
  success: CheckCircle,
  error: AlertCircle,
  warning: AlertTriangle,
  info: Info
}
</script>

<style scoped>
.toast-enter-active,
.toast-leave-active {
  transition: all 0.3s ease;
}

.toast-enter-from {
  opacity: 0;
  transform: translateX(100%);
}

.toast-leave-to {
  opacity: 0;
  transform: translateX(100%) scale(0.9);
}
</style>

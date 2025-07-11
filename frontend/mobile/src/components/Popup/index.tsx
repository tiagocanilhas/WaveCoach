import * as React from 'react'
import { Modal, View, Text, TouchableOpacity } from 'react-native'
import Ionicons from '@expo/vector-icons/Ionicons'

import { MainView } from '@components/MainView'
import { Button } from '@components/Button'

import { styles } from './styles'

type PopupProps = {
  title?: string
  content: React.ReactNode
  onClose: () => void
}

export function Popup({ title, content, onClose }: PopupProps) {
  return (
    <View style={styles.overlay}>
      <Modal transparent animationType="slide" onRequestClose={onClose}>
        <MainView style={styles.container}>
          <View style={styles.popup}>
            {title && <Text style={styles.title}>{title}</Text>}
            <TouchableOpacity style={styles.closeButton} onPress={onClose}>
              <Ionicons name="close" size={24} color="#333" />
            </TouchableOpacity>
            <View style={styles.content}>{content}</View>
          </View>
        </MainView>
      </Modal>
    </View>
  )
}

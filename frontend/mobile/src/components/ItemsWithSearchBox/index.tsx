import * as React from 'react'
import { ScrollView, View, TouchableOpacity } from 'react-native'
import { MaterialIcons } from '@expo/vector-icons'

import { MainView } from '@components/MainView'
import { Input } from '@components/Input'

import { styles } from './styles'

export type ItemsWithSearchBoxProps<T> = {
  nameProperty: string
  value: string
  onChange: (value: string) => void
  items: T[]
  isExtended?: boolean
  toggleExtended?: () => void
  renderItem: (item: T) => React.ReactNode
}

export function ItemsWithSearchBox<T>({
  nameProperty,
  value,
  onChange,
  items,
  renderItem,
  isExtended,
  toggleExtended,
}: ItemsWithSearchBoxProps<T>) {
  const filtered = items.filter(item => {
    if (typeof item === 'object' && item !== null) {
      const itemValue = (item as any)[nameProperty]
      return itemValue && itemValue.toLowerCase().includes(value.toLowerCase())
    }
    return false
  })

  return (
    <MainView style={styles.container}>
      <View style={styles.header}>
        <Input style={styles.input} value={value} placeholder="Search" onChange={onChange} />
        { toggleExtended !== undefined && (
            <TouchableOpacity style={styles.button} onPress={toggleExtended}>
              <MaterialIcons name={isExtended ? 'view-agenda' : 'view-module'} size={24} color="black" />
            </TouchableOpacity>
        )}
      </View>
      <ScrollView style={styles.scrollContainer}>
        <View style={[styles.itemsContainer, isExtended ? styles.extended : styles.notExtended]}>
          {filtered.map(item => renderItem(item))}
        </View>
      </ScrollView>
    </MainView>
  )
}

import * as React from 'react';
import { View, Text, TouchableOpacity } from 'react-native';
import Ionicons from '@expo/vector-icons/Ionicons';

import { styles } from './styles';

type HeaderProps = {
    title?: string;
    onBackPress?: () => void;
}

export function Header({ title, onBackPress }: HeaderProps) {
  return (
    <View style={styles.container}>
      {onBackPress && (
        <TouchableOpacity onPress={onBackPress} style={styles.backButton}>
          <Ionicons name="arrow-back" size={24} color={styles.backButtonColor} />
        </TouchableOpacity>
      )}
      {title && (
                <View style={styles.titleContainer}>
          <Text style={styles.title} numberOfLines={1}>
            {title}
          </Text>
        </View>
      )}
    </View>
  );
}
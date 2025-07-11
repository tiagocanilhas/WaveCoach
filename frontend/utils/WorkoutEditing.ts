export class WorkoutEditing {
  static checkCreateObject<T extends { id: any }>(object: T): boolean {
    if (object.id != null) return false;
    
    return true;
  }

  static checkEditObject<T extends { id: any }>(object: T): boolean {
    if (object.id == null) return false;

    const hasNonNullField = Object.entries(object)
      .filter(([key]) => key !== "id")
      .some(([_, value]) => value != null);

    if (!hasNonNullField) return false;
    
    return true;
  }

  static checkDeleteObject<T extends { id: any }>(object: T): boolean {
    if (object.id == null) return false;
    
    const allOthersNull = Object.entries(object)
      .filter(([key]) => key !== "id")
      .every(([_, value]) => value == null);

    if (!allOthersNull) return false;
    
    return true;
  }

  static nullifyFieldsExceptId<T extends { id: any }>(object: T): T {
    return Object.fromEntries(
        Object.entries(object)
          .map(([key, value]) => key === "id" ? [key, value] : [key, null])
        ) as T;
    }

  static noEditingMade<T extends { id: any }>(object: T): boolean {
    if (object.id == null) return false

    function isNullOrUndefined (value: any){ return value == null || value === undefined }

    const allFieldsNull = Object.entries(object)
      .filter(([key]) => key !== "id")
      .every(([_, value]) => {
        if (Array.isArray(value)) return value.every(isNullOrUndefined)
        return isNullOrUndefined(value)
      })

    return allFieldsNull
  }

  static onlyIfDifferent<T>(key: string, current: T, original: T): T[keyof T] | null {
    return current[key] === original[key] ? null : current[key];
  }

  static checkOrder(index: number, oldValue: number | null): number | null {
    const newValue = index + 1
    switch (oldValue) {
        case null:
            return null
        case newValue:
            return null
        default:
            return newValue
    }
  }

  static separateList<T extends { id: any }>(list: T[]): { editable: T[]; removed: T[] } {
    return list.reduce((acc, item) => {
        if (this.checkDeleteObject(item)) acc.removed.push(item)
        else acc.editable.push(item)
        return acc
      }, { editable: [] as T[], removed: [] as T[] })
    }
}

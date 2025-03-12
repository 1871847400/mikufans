export async function generateFile(imgUrl: string, imgName = '1.jpg') {
  try {
    const res = await fetch(imgUrl)
    if (res.ok) {
      const blob = await res.blob()
      return new File([blob], imgName, {
        type: 'image/jpg'
      })
    }  
  } catch(err) {
  }
  return null
}
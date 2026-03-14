import { DTable } from './dtable'
import './style.css'
import './styles/doodle.css'
// import "doodle.css/doodle.css"

const get = async (url: string) => await (await fetch('/api/' + url)).json() 
const post = async (url: string, obj: any) => await (await fetch('/api/' + url, {
	method: "POST",
	headers: { 'Content-Type': 'application/json' },
	body: JSON.stringify(obj)
})).json()

type MetaProperty = { id: number, name: string, setter: boolean, type: string }
type Constructor = { id: number, params: { name: string, type: string }[] }
type Method = { id: number, name: string, params: { name: string, type: string }[] }
type Meta = {
	methods: Method[]
	properties: MetaProperty[]
	constructors: Constructor[]
}

console.log('Main start')

const eBody = <div className="body"></div> as HTMLElement;

(async () => {
	document.body.appendChild(eBody)
	const types = await get('types') as string[]

	const eDatabase = <div></div>

	const selectDatabase = async (type: string) => {
		eDatabase.innerHTML = ""
		const meta = await get('meta/' + type) as Meta
		const list = await get('obj/' + type) as any[]
		console.log(meta)

		const table = new DTable()
		meta.properties.forEach(p => table.addHeader(p.name))
		meta.methods.forEach(p => table.addHeader(p.name))
		table.addHeader("")
		const put = (e: any) => {
			meta.properties.forEach(prop => {
				let src = e[prop.name.toLowerCase()]
				if(!prop.setter) return table.addCell(<div>{src}</div>)
				const input = <input value={src} /> as HTMLInputElement
				const container = <div class="input" tabIndex={1}>{input}</div>
				input.onfocus = () => {
					container.classList.remove('err')
					container.setAttribute('focus', '1')
				}
				const save = () => {
					const string = prop.type.toLowerCase().includes('string')
					const value: any = string ? input.value : parseFloat(input.value)
					if(!string && (typeof (value) !== 'number' || isNaN(value))) {
						input.parentElement?.classList.add('err')
						return
					}
					if(src == value) {
						container.classList.remove('err')
						return
					}
					(async () => {
						try {
							const res = (await post(`prop/${e['id']}/${prop.id}`, value)).result
							console.log(res)
							src = value
							container.classList.remove('err')
						} catch(e) {
							container.classList.add('err')
						}
					})()
				}
				input.onblur = () => {
					container.setAttribute('focus', '0')
					save()
				}
				table.addCell(container)
			})
			meta.methods.forEach((m, mid) => {
				const btn = <button className="call" onClick={() => {
					const parms: any = {};
					// TODO: request parms
					(async () => {
						const res = (await post(`call/${e['id']}/${m.id}`, parms)).result
						// alert(res)
						btn.textContent = res
					})()
				}}>???</button>
				table.addCell(btn)
			})
			const row = table.lastRow()
			table.addCell(<button className="remove" onClick={() => {
				(async () => {
					table.remove(row)
					const res = (await get(`delete/${e['id']}`))
					console.log(res)
				})()
			}}/>)
		}
		list.forEach(e => put(e))

		eDatabase.append(<h1>Constructors</h1>)
		meta.constructors.forEach(c => {
			const constructor = <div className="database-constructor border"></div>
			const inputs: HTMLInputElement[] = c.params.map((p, pid) => {
				const input = <input id={`${c.id}-${pid}`} />
				const container = <div class="input" tabIndex={1}>{input}</div>
				input.onfocus = () => {
					container.classList.remove('err')
					container.setAttribute('focus', '1')
				}
				input.onblur = () => container.setAttribute('focus', '0')
				constructor.append(<label htmlFor={input.id}>{p.name}</label>)
				constructor.append(container)
				return input as HTMLInputElement
			})
			constructor.append(<button className="ok" onClick={() => {
				const obj: any = {}
				let ok = true
				c.params.forEach((p, pid) => {
					const input = inputs[pid]
					const string = p.type.toLowerCase().includes('string')
					const value: any = string ? input.value : parseFloat(input.value)
					if(!string && (typeof (value) !== 'number' || isNaN(value))) {
						input.parentElement?.classList.add('err')
						ok = false
					}
					obj[p.name] = value
				})
				if(ok) {
					(async () => {
						const res = await post(`create/${type}/${c.id}`, obj)
						console.log(res)
						put(res)
						table.rebuild()
					})()
				}
			}}>Create</button>)
			eDatabase.append(constructor)
			eDatabase.append(<br />)
			eDatabase.append(<br />)
		})

		eDatabase.append(<h1>Table</h1>)
		eDatabase.append(table.build())
	}
	
	eBody.append(<h1>Database</h1>)

	const tabs: HTMLElement[] = []
	types.forEach((t, tid) => {
		const eButton = <button>{t}s</button> as HTMLElement
		tabs.push(eButton)
		eButton.onclick = () => {
			selectDatabase(t)
			tabs.forEach(t => t.classList.remove('selected'))
			eButton.classList.add('selected')
			localStorage.setItem('tab', `${tid}`)
		}
	})
	try {
		tabs[parseInt(localStorage.getItem('tab') ?? "")].click()
	} catch(e) {}
	eBody.append(<div className="database-tabs">{tabs}</div>)
	eBody.append(eDatabase)


})().catch(e => console.error(e))

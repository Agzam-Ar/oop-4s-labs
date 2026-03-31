import { ReactElement } from "jsx-dom"


export class DTable {

	private header:HTMLElement[] = []
	private cells:HTMLElement[][] = [this.header]
	private container:HTMLElement = <div className="database-table" style={{display: 'grid'}}>{}</div> as HTMLElement

	public constructor() {}

	public addHeader(name: string, element?:HTMLElement) {
		if(!element) {
			element = <div className="database-header">{name}</div> as HTMLElement;
		}
		this.header.push(element)
	}

	public addCell(element:HTMLElement|ReactElement) {
		let row = this.lastRow()
		if(row.length >= this.header.length) {
			row = []
			this.cells.push(row)
		}
		row.push(element as HTMLElement)
	}

	public remove(row: HTMLElement[]) {
		const index = this.cells.indexOf(row);
		if (index !== -1) {
		    this.cells.splice(index, 1);
		    this.rebuild()
		}
	}

	public lastRow() {
		return this.cells[this.cells.length-1]
	}

	public rebuild() {

		const eCols = this.header.map((_,id) => <div className="database-col" style={{display: 'contents'}}/>)

		this.cells.forEach((row,rid) => {
			row.forEach((cell,cid) => {
				cell.style.gridRow = `${rid+1}`
				cell.style.gridColumn = `${cid*2+2}`
				eCols[rid].appendChild(cell);
			})
		})

		const eTable = this.container;
		eTable.innerHTML = ""
		let styleTemplate = `5px`
		const gridRow = `1/${this.cells.length+1}`
		eTable.append(<div className="database-separator" style={{gridColumn: 1, gridRow: gridRow}}></div> as HTMLElement)
		eCols.forEach((c,cid) => {
			eTable.append(c)
			eTable.append(<div className="database-separator" style={{gridColumn: cid*2+3, gridRow: gridRow}}></div> as HTMLElement)
			styleTemplate += ' auto 5px'
		})
		eTable.style.gridTemplateColumns = styleTemplate
			// tableChilds.push(<div className="database-separator"></div> as HTMLElement)

		return eTable;
	}

	public build() {
		this.rebuild()
		return this.container;
	}

}